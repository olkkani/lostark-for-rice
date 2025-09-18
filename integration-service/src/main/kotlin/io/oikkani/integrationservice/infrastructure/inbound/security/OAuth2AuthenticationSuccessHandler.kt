package io.oikkani.integrationservice.infrastructure.inbound.security

import com.github.f4b6a3.tsid.TsidCreator
import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.domain.dto.DiscordUser
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.JwtTokenClient
import io.olkkani.common.dto.contract.RefreshToken
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val oauth2client: JwtTokenClient,
    //TODO: 토큰의 만료기간을 5분으로 제한
    @param:Value("\${secret.jwt.expiration:2592000000}") private val refreshTokenExpiration: Long,
    @param:Value("\${frontend.domain:http://localhost:5173}")
    private val frontendDomain: String,
) : AuthenticationSuccessHandler {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val logger = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val frontendSuccessUrl = "$frontendDomain/auth/success"

        try {
            val oAuth2User = authentication.principal as OAuth2User
            val discordUser = mapToDiscordUser(oAuth2User)
            val deviceId = TsidCreator.getTsid256().toString()
            val clientId = "${discordUser.id}:${deviceId}"

            // JWT 토큰 생성 (Discord ID를 사용자 식별자로 사용)
            val accessToken = jwtTokenProvider.generateAccessToken(userName = discordUser.id)
            val refreshToken = TsidCreator.getTsid().toString()

            // Refresh Token 저장
            saveRefreshToken(clientId, refreshToken)

            // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
            // TODO: Token 보안이 제대로 적용된건지 확인
            val redirectUrl = UriComponentsBuilder.fromUriString(frontendSuccessUrl)
                .queryParam("accessToken", URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
                .build()
                .toUriString()
            // Refresh Token을 HttpOnly 쿠키로 설정
            response.addCookie(createHttpOnlyCookie(key = "refreshToken", value = refreshToken))
            response.addCookie(createHttpOnlyCookie(key = "deviceId", value = deviceId))
            response.sendRedirect(redirectUrl)

        } catch (e: Exception) {
            logger.error { "OAuth2 Success Handler Error: ${e.message}" }
            e.printStackTrace()
            // 에러 발생 시 실패 페이지로 리다이렉트
            response.sendRedirect("$frontendSuccessUrl?error=oauth_failed")
        }
    }

    private fun mapToDiscordUser(oAuth2User: OAuth2User): DiscordUser {
        return DiscordUser(
            id = oAuth2User.getAttribute<String>("id") ?: throw IllegalStateException("Discord ID not found"),
            username = oAuth2User.getAttribute<String>("username") ?: "Unknown",
            discriminator = oAuth2User.getAttribute<String>("discriminator") ?: "0000",
            email = oAuth2User.getAttribute<String>("email"),
            avatar = oAuth2User.getAttribute<String>("avatar"),
            verified = oAuth2User.getAttribute<Boolean>("verified")
        )
    }

    private fun saveRefreshToken(userId: String, refreshToken: String) {
        val token = RefreshToken(
            clientId = userId,
            token = refreshToken
        )
        coroutineScope.launch { oauth2client.save(token) }
    }

    private fun createHttpOnlyCookie(key: String, value: String): Cookie {
        return Cookie(key, value).apply {
            maxAge = (refreshTokenExpiration / 1000L).toInt()
            secure = true
            isHttpOnly = true
            path = "/"
            setAttribute("SameSite", "Strict")
        }
    }
}