package io.oikkani.processorservice.application.port.inbound

interface TokenUseCase {
    fun deleteToken(token: String)
    fun isValidToken(token: String): Boolean
}