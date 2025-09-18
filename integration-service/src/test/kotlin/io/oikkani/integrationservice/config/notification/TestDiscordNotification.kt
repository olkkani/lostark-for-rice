package io.oikkani.integrationservice.config.notification

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AlertError
import io.oikkani.integrationservice.infrastructure.outbound.notofication.dto.DiscordWebhookResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Component
class TestDiscordNotification(
    @param:Value("\${webhook.url.discord:must_not_empty_url}") private val webhookUrl: String
) : ExceptionNotification {

    private val webClient: WebClient = WebClient.builder().baseUrl(webhookUrl).build()

    override fun sendErrorNotification(alertError: AlertError) {
        val message = DiscordWebhookResponse(
            content = "테스트 서버 에러 발생",
            embeds = listOf(
                DiscordWebhookResponse.Embed(
                    title = "Test Error Action: ${alertError.actionName}",
                    description = """
                        **Error State**: ${alertError.errorCode} ${alertError.errorStatus}
                        **Retry attempts**: ${alertError.retryAttempts}
                        **Time Stamp:** ${LocalDateTime.now()}
                        **Stack Trace:**
                        ```
                        ${alertError.errorMessage}
                        ```
                    """.trimIndent()
                )
            )
        )

        // ⭐ 동기적으로 실행하여 테스트가 완료를 기다림
        runBlocking {
            webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .toBodilessEntity()
                .awaitSingleOrNull()
        }
    }
}
