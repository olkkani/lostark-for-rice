package io.oikkani.integrationservice.infrastructure.outbound.client.lostark

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.BaseClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.request.MarketRequest
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.response.MarketResponse
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class MarketClient(
    @Value("\${lostark.auction.api.key:must-not-null-auction-apikey}") apiKey: String,
    private val exceptionNotification: ExceptionNotification
) : BaseClient(exceptionNotification) {
    private val baseUrl: String = "https://developer-lostark.game.onstove.com"

    val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    private fun fetchItems(marketRequest: MarketRequest): Mono<MarketResponse> {
        return client.post()
            .uri("/markets/items")
            .bodyValue(marketRequest)
            .retrieve()
            .bodyToMono(MarketResponse::class.java)
            .withCommonRetry("fetch_auction_items")
    }

    suspend fun fetchItemsAsync(marketRequest: MarketRequest): MarketResponse? {
        return fetchItems(marketRequest).awaitSingleOrNull()
    }
}
