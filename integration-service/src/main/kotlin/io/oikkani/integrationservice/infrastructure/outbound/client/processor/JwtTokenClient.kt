package io.oikkani.integrationservice.infrastructure.outbound.client.processor

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.outbound.client.BaseClient
import io.olkkani.common.dto.contract.RefreshToken
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class JwtTokenClient(
    @param:Value("\${processor.url:must-not-null-processor-url}") private val processorServiceUrl: String,
    private val exceptionNotification: ExceptionNotification,
) : BaseClient(exceptionNotification) {

    val client: WebClient = WebClient.builder()
        .baseUrl(processorServiceUrl)
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
            .withCommonRetry()
            .awaitSingleOrNull()
    }
    //todo fire-forget 형식으로 변환
    suspend fun delete(token: String) {
        client.delete()
            .uri("/token/{token}", token)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetry()
            .awaitSingleOrNull()
    }

    suspend fun isExpired(tokenEntity: RefreshToken): Boolean {
        return client.post()
            .uri("/token/isExpired")
            .bodyValue(tokenEntity)
            .retrieve()
            .bodyToMono(Boolean::class.java)
            .withCommonRetry()
            .awaitSingleOrNull() ?: false
    }
}