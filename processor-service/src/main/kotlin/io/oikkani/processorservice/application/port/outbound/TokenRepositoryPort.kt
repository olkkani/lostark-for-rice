package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.RefreshToken

interface TokenRepositoryPort{
    fun save(token: RefreshToken)
    fun isExpired(token: RefreshToken): Boolean
    fun deleteByClientId(clientId: String)
    fun deleteByToken(token: String)
}