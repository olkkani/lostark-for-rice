package io.oikkani.integrationservice.infrastructure.adapter.out.notofication.dto

data class DiscordUserDto(
    val id: String,
    val username: String,
    val discriminator: String,
    val email: String?,
    val avatar: String?,
    val verified: Boolean?
)