package io.olkkani.lfr.dto

data class DiscordWebhookResponse(
    val content: String,
    val embeds: List<Embed>? = null
) {
    data class Embed(
        val title: String,
        val description: String
    )
}