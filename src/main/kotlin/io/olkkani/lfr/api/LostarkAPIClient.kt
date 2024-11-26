package io.olkkani.lfr.api

import io.olkkani.lfr.model.AuctionRequest
import io.olkkani.lfr.model.AuctionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

class LostarkAPIClient(
    private val apiKey: String
) {
    private val baseUrl: String = "https://developer-lostark.game.onstove.com"
    private val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "barer $apiKey")
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun getAuctionItems(auctionRequest: AuctionRequest): Flux<AuctionResponse> {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToFlux(AuctionResponse::class.java)
            .onErrorResume { error ->
                println("Error occurred: ${error.message}")
                Flux.empty()
            }
    }
}