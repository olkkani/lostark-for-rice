package io.olkkani.lfr.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.AuctionRequest
import io.olkkani.lfr.dto.AuctionResponse
import io.olkkani.lfr.dto.MarketRequest
import io.olkkani.lfr.dto.MarketResponse
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class LostarkAPIClient(
    @Value("\${lostark.api.key:key}") private val apiKey: String,
    private val exceptionNotification: ExceptionNotification,
) {
    private val logger = KotlinLogging.logger {}
    private val baseUrl: String = "https://developer-lostark.game.onstove.com"
    private val client: WebClient = WebClient.builder()
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
            .onErrorResume { error ->
                exceptionNotification.sendErrorNotification(error.message.toString(), "fetch_auction_items_error")
                logger.error {"Error occurred: ${error.message}"}
                Mono.empty()
            }
    }
    suspend fun fetchAuctionItemsSubscribe(auctionRequest: AuctionRequest): AuctionResponse {
        return fetchAuctionItems(auctionRequest)
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }
    private fun fetchMarketItem(marketRequest: MarketRequest): Mono<MarketResponse> {
        return client.post()
            .uri("/market/items")
            .bodyValue(marketRequest)
            .retrieve()
            .bodyToMono(MarketResponse::class.java)
            .onErrorResume { error ->
                exceptionNotification.sendErrorNotification(error.message.toString(), "fetch_market_item_error")
                logger.error {"Error occurred: ${error.message}"}
                Mono.empty()
            }
    }
    suspend fun fetchMarketItemPriceSubscribe(marketRequest: MarketRequest): MarketResponse? {
        return fetchMarketItem(marketRequest)
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingleOrNull()
    }
}