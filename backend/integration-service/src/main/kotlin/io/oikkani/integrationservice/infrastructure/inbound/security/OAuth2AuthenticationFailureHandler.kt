package io.oikkani.integrationservice.infrastructure.inbound.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler(
    @param:Value("\${service.domain:localhost:8080}") private val serviceDomain: String,
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val frontendFailureUrl = "$serviceDomain/auth/failure"
        response.sendRedirect("$frontendFailureUrl?error=${exception.message}")
    }
}