package io.oikkani.processorservice.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("refresh_token", timeToLive = 2592000000)
class RefreshToken(
    @Id
    val clientId: String,
    val token: String,
)