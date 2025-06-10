package io.olkkani.lfr.service

import org.springframework.stereotype.Service

interface TokenService {
    fun deleteToken(token: String)
    fun isValidToken(token: String): Boolean
}

@Service
class TokenServiceImpl {
}