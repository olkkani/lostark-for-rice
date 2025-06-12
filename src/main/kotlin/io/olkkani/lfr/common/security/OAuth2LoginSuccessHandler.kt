package io.olkkani.lfr.common.security

import com.github.f4b6a3.tsid.TsidCreator
import io.olkkani.lfr.adapter.external.dto.DiscordUserDto
import io.olkkani.lfr.repository.TokenRepo
import io.olkkani.lfr.repository.entity.RefreshToken
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2LoginSuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenRepo: TokenRepo,
    @Value("\${frontend.domain:http://localhost:5173}")
    private val frontendDomain: String
) : AuthenticationSuccessHandler {

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
                .queryParam("access_token", URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
                .queryParam("device_id", URLEncoder.encode(deviceId, StandardCharsets.UTF_8))
                .build()
                .toUriString()

            // Refresh Token을 HttpOnly 쿠키로 설정
            response.addCookie(createHttpOnlyCookie(refreshToken))
            response.sendRedirect(redirectUrl)

        } catch (e: Exception) {
            // 에러 발생 시 실패 페이지로 리다이렉트
            response.sendRedirect("$frontendSuccessUrl?error=oauth_failed")
        }
    }

    private fun mapToDiscordUser(oAuth2User: OAuth2User): DiscordUserDto {
        return DiscordUserDto(
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
        tokenRepo.save(token)
    }

    private fun createHttpOnlyCookie(value: String): Cookie {
        return Cookie("refresh_token", value).apply {
            maxAge = 604800  // 7 days
            secure = true
            isHttpOnly = true
            path = "/"
            setAttribute("SameSite", "Lax")
        }
    }
}
