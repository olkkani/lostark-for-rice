package io.oikkani.integrationservice.application.port.out

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

abstract class ProcessorBaseClient {

    protected val maxRetryAttempts = 3L
    protected val initialDelay: Duration = Duration.ofSeconds(1)
    protected val maxBackoff: Duration = Duration.ofSeconds(5)
    protected val requestTimeout: Duration = Duration.ofSeconds(30)

    private val baseUrl: String = ""
    protected val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()



}