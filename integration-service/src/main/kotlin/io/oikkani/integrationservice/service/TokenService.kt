package io.oikkani.integrationservice.service

import org.springframework.stereotype.Service

interface TokenService {
    fun deleteToken(token: String)
    fun isValidToken(token: String): Boolean
}

@Service
class TokenServiceImpl {
}