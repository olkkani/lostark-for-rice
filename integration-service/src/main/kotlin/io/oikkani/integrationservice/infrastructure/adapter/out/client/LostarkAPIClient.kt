package io.oikkani.integrationservice.infrastructure.adapter.out.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.external.dto.AuctionResponse
import io.oikkani.integrationservice.external.dto.MarketRequest
import io.oikkani.integrationservice.external.dto.MarketResponse
import io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.request.AuctionRequest
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

class LostarkAPIClient(
    private val apiKey: String,
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
            .uri("/markets/items")
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