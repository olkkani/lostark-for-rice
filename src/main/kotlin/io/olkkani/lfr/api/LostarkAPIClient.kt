package io.olkkani.lfr.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.model.AuctionRequest
import io.olkkani.lfr.model.ResponseData
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

    fun getAuctionItems(auctionRequest: AuctionRequest): Mono<List<Int>>  {
        return client.post()
            .uri("/auctions/items")
            .bodyValue(auctionRequest)
            .retrieve()
            .bodyToMono(ResponseData::class.java)
            .map { response ->
                response.items.map { it.auctionInfo.buyPrice }
            }
            .onErrorResume { error ->
                logger.error {"Error occurred: ${error.message}"}
                Mono.empty()
            }
    }
}