package io.oikkani.integrationservice.infrastructure.adapter.inbound.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationFailureHandler(
    @param:Value("\${frontend.domain:http://localhost:5173}")
    private val frontendDomain: String
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val frontendFailureUrl = "$frontendDomain/auth/failure"
        response.sendRedirect("$frontendFailureUrl?error=${exception.message}")
    }
}