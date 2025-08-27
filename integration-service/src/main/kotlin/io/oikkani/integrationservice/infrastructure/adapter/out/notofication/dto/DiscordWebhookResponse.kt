package io.oikkani.integrationservice.infrastructure.adapter.out.notofication.dto

data class DiscordWebhookResponse(
    val content: String,
    val embeds: List<Embed>? = null
) {
    data class Embed(
        val title: String,
        val description: String
    )
}