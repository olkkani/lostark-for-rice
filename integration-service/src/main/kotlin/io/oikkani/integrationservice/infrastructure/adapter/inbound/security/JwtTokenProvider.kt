package io.oikkani.integrationservice.infrastructure.adapter.inbound.security

import com.github.f4b6a3.tsid.TsidCreator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AlertError
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.JwtTokenClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto.RefreshToken
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.servlet.View
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @param:Value("\${secret.jwt.secret:dVyUSSGqhfyWt5d23IZ1gS0P0OkjgZl03t20V32jfsjg}") private val secret: String,
    @param:Value("\${secret.jwt.expiration:604800000}") private val validityInMilliseconds: Long,
    private val tokenClient: JwtTokenClient,
    private val exceptionNotification: ExceptionNotification,
    private val error: View,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger = KotlinLogging.logger { }
    val secretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    fun generateAccessToken(userName: String): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Jwts.builder()
            .audience().add(userName).and()
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun getUsername(token: String): String {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .audience
            .first()
    }


    // Refresh Token 생성 및 저장
    fun generateRefreshToken(username: String, deviceId: String = "default"): String {
        val refreshToken = TsidCreator.getTsid().toString()
        val clientId = "$username:$deviceId"
        val tokenEntity = RefreshToken(clientId, refreshToken)

        coroutineScope.launch {
            // 기존 refresh token 삭제 (선택사항)
            tokenClient.delete(clientId)
            // 새 refresh token 저장
            tokenClient.save(tokenEntity)
        }

        return refreshToken
    }

    // Refresh Token 검증
    fun validateRefreshToken(username: String, refreshToken: String, deviceId: String = "default"): Boolean {
        val tokenEntity = RefreshToken("$username:$deviceId", refreshToken)
        val isExpired = runBlocking {
            tokenClient.isExpired(tokenEntity)
        }
        return isExpired
    }

    // Refresh Token으로 새 Access Token 발급
    fun refreshAccessToken(username: String, refreshToken: String, deviceId: String = "default"): String? {
        return if (validateRefreshToken(username, refreshToken, deviceId)) {
            generateAccessToken(username)
        } else {
            coroutineScope.launch { tokenClient.delete(refreshToken) }
            null
        }
    }

    // 로그아웃 시 Refresh Token 삭제
    fun revokeRefreshToken(username: String, deviceId: String = "default") {
        val clientId = "$username:$deviceId"
        coroutineScope.launch { tokenClient.delete(clientId) }
    }


    fun getAuthentication(token: String): Authentication {
        val userDetails: UserDetails = getUserDetails(token)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private fun getUserDetails(token: String): UserDetails {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        val username = claims.subject

        @Suppress("UNCHECKED_CAST")
        val roles = claims["roles"] as? List<String> ?: listOf("USER")
        val authorities = roles.map { SimpleGrantedAuthority(it) }.toMutableList<GrantedAuthority>()

        return User(username, "", authorities)
    }


    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    fun isExpired(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            // 토큰 만료 확인
            val expiration = claims.expiration
            val now = Date()

            // 필수 클레임 확인 (audience가 있고 비어있지 않은지)
            val audience = claims.audience
            !expiration.before(now) && audience.isNotEmpty()
        } catch (e: Exception) {
            logger.error { e.toString() }
            val alertError = AlertError(
                actionName = "jwt_token_valiediton_failed",
                errorCode = e.hashCode(),
                errorStatus = e.javaClass.simpleName,
                errorMessage = e.message ?: "Unknown error"
            )
            coroutineScope.launch {
                exceptionNotification.sendErrorNotification(alertError)
                tokenClient.delete(token)
            }
            false
        }
    }
}