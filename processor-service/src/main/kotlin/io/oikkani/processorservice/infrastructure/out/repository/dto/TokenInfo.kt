package io.oikkani.processorservice.infrastructure.out.repository.dto

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpirationTime: Long
)