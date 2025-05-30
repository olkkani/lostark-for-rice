package io.olkkani.lfr.repository

import io.olkkani.lfr.repository.entity.RefreshToken
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.Duration


@Repository
interface TokenRepo : CrudRepository<RefreshToken, Long>, TokenRepoSupport {
    fun existsByUserAndDeviceIdAndRefreshToken(userAndDeviceId: String, refreshToken: String): Boolean
}

interface TokenRepoSupport {

}

@Repository
class TokenRepoSupportImpl(
    private val redisTemplate: RedisTemplate<String, String>
) : TokenRepoSupport {
    companion object {
        private const val KEY_PREFIX = "refresh_token:"
        private val TTL = Duration.ofDays(7)
    }

    fun save(userName: String, refreshToken: String) {
        redisTemplate.opsForValue().set(
            "$KEY_PREFIX$userName",
            refreshToken,
            TTL
        )
    }
}
