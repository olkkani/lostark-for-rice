package io.oikkani.integrationservice.infrastructure.outbound.client.processor

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

abstract class ProcessorBaseClient {

    private val baseUrl: String = "localhost:8080"
    protected val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()


    protected suspend fun <T> subscribeSingle(mono: Mono<T>): T {
        return mono
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }

    protected suspend fun <T> subscribeSingle1(mono: Mono<T>) {
        mono
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }
}