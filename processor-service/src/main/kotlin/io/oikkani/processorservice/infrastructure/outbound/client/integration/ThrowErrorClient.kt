package io.oikkani.processorservice.infrastructure.outbound.client.integration

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ThrowErrorClient(
    @param:Value("\${integration.url:localhost:8081}")
    private val integrationUrl: String,
) {

    val client: WebClient = WebClient.builder()
        .baseUrl(integrationUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()


    suspend fun throwError() {
        client.post()
    }
}