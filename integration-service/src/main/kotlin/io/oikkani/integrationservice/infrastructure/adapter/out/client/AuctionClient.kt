// infrastructure/adapter/out/client/AuctionClient.kt
package io.oikkani.integrationservice.infrastructure.adapter.out.client

import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.request.AuctionRequest
import io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.response.AuctionResponse
import io.oikkani.integrationservice.infrastructure.config.WebClientRetryPolicy.withCommonRetry
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class AuctionClient(
    @Value("\${lostark.auction.api.key:must-not-null-auction-apikey}") apiKey: String,
    exceptionNotification: ExceptionNotification,
) : BaseLostarkClient(exceptionNotification) {

    private val baseUrl: String = "https://developer-lostark.game.onstove.com"

    val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    private fun fetchAuctionItems(auctionRequest: AuctionRequest): Mono<AuctionResponse> {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToMono(AuctionResponse::class.java)
            .withCommonRetry(exceptionNotification, "fetch_auction_items_error")
    }

    suspend fun fetchAuctionItemsAsync(auctionRequest: AuctionRequest): AuctionResponse {
        return subscribeSingle(fetchAuctionItems(auctionRequest))
    }
}



