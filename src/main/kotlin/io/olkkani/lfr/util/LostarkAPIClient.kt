package io.olkkani.lfr.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.AuctionRequest
import io.olkkani.lfr.dto.AuctionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class LostarkAPIClient(
    private val apiKey: String
) {
    private val logger = KotlinLogging.logger {}
    private val baseUrl: String = "https://developer-lostark.game.onstove.com"
    private val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "bearer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun fetchAuctionItems(auctionRequest: AuctionRequest): Mono<AuctionResponse> {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToMono(AuctionResponse::class.java)
            .onErrorResume { error ->
                logger.error {"Error occurred: ${error.message}"}
                Mono.empty()
            }
    }

    fun fetchAuctionItemsSynchronously(auctionRequest: AuctionRequest): AuctionResponse? {
        return fetchAuctionItems(auctionRequest).block()
    }
}