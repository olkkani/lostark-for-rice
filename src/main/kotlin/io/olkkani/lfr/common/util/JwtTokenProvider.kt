package io.olkkani.lfr.common.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider(
    @Value("\${secret.jwt.secret}") private val secret: String,
    @Value("\${secret.jwt.expiration}") private val validityInMilliseconds: Long
) {
    private val logger = KotlinLogging.logger {  }
    val secretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret))
    fun createToken(userName: String): String {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        return Jwts.builder()
            .audience().add(userName).and()
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact()
    }

    fun getUserNameByToken(token: String): String{
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .audience
            .first()
    }
    fun validateToken(token: String): Boolean {
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
            // 서명 검증 실패, 토큰 형식 오류, 만료된 토큰 등 모든 예외 처리
            logger.error { e.toString() }
            false
        }
    }
}