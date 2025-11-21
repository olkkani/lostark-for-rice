package io.oikkani.integrationservice.domain.dto

data class DiscordUser(
    val id: String,
    val username: String,
    val discriminator: String,
    val email: String?,
    val avatar: String?,
    val verified: Boolean?
)