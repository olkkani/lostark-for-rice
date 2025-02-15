package io.olkkani.lfr.service

import io.olkkani.lfr.dto.DiscordWebhookResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class DiscordExceptionNotificationImpl(
    @Value("\${webhook.url.discord}") private val webhookUrl: String
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
                        **Time Stamp:** ${java.time.LocalDateTime.now()}
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