package io.olkkani.lfr.repository.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("refresh_token", timeToLive = 604800)
class RefreshToken(
    @Id
    val clientId: String,
    val token: String,
)