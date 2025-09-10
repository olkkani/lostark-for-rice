package io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AlertErrorDTO
import io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication.dto.DiscordWebhookResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Component
class DiscordExceptionNotificationImpl(
    @param:Value("\${webhook.url.discord:must_not_empty_url}") private val webhookUrl: String
) : ExceptionNotification {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val webClient: WebClient = WebClient.builder().baseUrl(webhookUrl).build()

    override fun sendErrorNotification(alertError: AlertErrorDTO) {
        val message = DiscordWebhookResponse(
            content = "서버 에러 발생",
            embeds = listOf(
                DiscordWebhookResponse.Embed(
                    title = "Error Action: ${alertError.actionName}",
                    description = """
                        **Error State**: ${alertError.errorCode.toString()} ${alertError.errorStatus}
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
        // WebClient 를 통해 비동기 POST 요청
        coroutineScope.launch {
            webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .toBodilessEntity()
                .awaitSingleOrNull()
        }
    }
}