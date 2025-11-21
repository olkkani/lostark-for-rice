package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.TokenRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.RefreshToken
import io.oikkani.processorservice.infrastructure.outbound.repository.redis.TokenRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.redis.TokenTemplateRepository

class TokenRepositoryAdaptor(
    private val repository: TokenRepository,
    private val templateRepository: TokenTemplateRepository,
) : TokenRepositoryPort {
    override fun save(token: RefreshToken) {
        repository.save(token)
    }

    override fun isExpired(token: RefreshToken): Boolean {
        return templateRepository.isExpired(token.token)
    }

    override fun deleteByClientId(clientId: String) {
        repository.deleteByClientId(clientId)
    }

    override fun deleteByToken(token: String) {
        repository.deleteByToken(token)
    }

}