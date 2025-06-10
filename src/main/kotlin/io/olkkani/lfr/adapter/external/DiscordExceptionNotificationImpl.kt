package io.olkkani.lfr.adapter.external

import io.olkkani.lfr.adapter.external.dto.DiscordWebhookResponse
import io.olkkani.lfr.common.util.ExceptionNotification
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Component
class DiscordExceptionNotificationImpl(
    @Value("\${webhook.url.discord:must_not_empty_url}") private val webhookUrl: String
) : ExceptionNotification {

    private val webClient: WebClient = WebClient.builder().baseUrl(webhookUrl).build()

    override fun sendErrorNotification(errorMessage: String, actionName: String) {
        val message = DiscordWebhookResponse(
            content = ":rotating_light: 서버 에러 발생!",
            embeds = listOf(
                DiscordWebhookResponse.Embed(
                    title = "에러 상세 정보",
                    description = """
                        **Function Name:** $actionName
                        **Time Stamp:** ${LocalDateTime.now()}
                        **Stack Trace:**
                        ```
                        $errorMessage
                        ```
                    """.trimIndent()
                )
            )
        )
        // WebClient 를 통해 비동기 POST 요청
        webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(message)
            .retrieve()
            .bodyToMono(Void::class.java)
            .subscribe()
    }
}