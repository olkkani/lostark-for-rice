package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.BaseClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto.RefreshToken
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class JwtTokenClient(
    private val exceptionNotification: ExceptionNotification,
) : BaseClient(exceptionNotification) {
    private val baseUrl = "https://api.oikkani.io/v1/oauth2"

    val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    //todo fire-forget 형식으로 변환
    suspend fun save(token: RefreshToken) {
        client.post()
            .uri("/tokens")
            .bodyValue(token)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe("token:save")
    }
    //todo fire-forget 형식으로 변환
    suspend fun delete(token: String) {
        client.delete()
            .uri("/tokens?token=$token")
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe("token:delete")
    }

    suspend fun isExpired(tokenEntity: RefreshToken): Boolean {
        return client.post()
            .uri("/tokens/isExpired")
            .bodyValue(tokenEntity)
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .withCommonRetry("token:is_expired")
            .awaitSingleOrNull() ?: false
    }
}