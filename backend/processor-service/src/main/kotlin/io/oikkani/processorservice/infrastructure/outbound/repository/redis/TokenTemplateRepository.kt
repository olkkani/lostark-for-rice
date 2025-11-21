package io.oikkani.processorservice.infrastructure.outbound.repository.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

fun interface TokenTemplateRepository {
    fun isExpired(token: String): Boolean
}


@Repository
class TokenTemplateRepositoryImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : TokenTemplateRepository {

    override fun isExpired(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}