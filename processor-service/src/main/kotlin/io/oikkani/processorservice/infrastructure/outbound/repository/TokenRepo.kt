package io.oikkani.processorservice.infrastructure.outbound.repository

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.RefreshToken
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface TokenRepo : CrudRepository<RefreshToken, String>, TokenRepoSupport {
 fun deleteByClientId(clientId: String)
 fun deleteByToken(token: String)
}

fun interface TokenRepoSupport {
     fun existTokenByClientIdAndToken(clientId: String, token: String): Boolean
}

@Repository
class TokenRepoSupportImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : TokenRepoSupport {
    companion object {
        private const val REDIS_KEY_PREFIX = "refresh_token:"
    }

    override fun existTokenByClientIdAndToken(clientId: String, token: String): Boolean {
        val redisKey = REDIS_KEY_PREFIX + clientId
        val storedToken = redisTemplate.opsForHash<String, String>()[redisKey, "token"]
        return storedToken == token
    }

}
