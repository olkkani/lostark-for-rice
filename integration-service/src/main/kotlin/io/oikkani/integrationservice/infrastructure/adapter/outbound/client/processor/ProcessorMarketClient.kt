package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor

import io.oikkani.integrationservice.domain.dto.AuctionListingPrice
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.BaseClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication.DiscordExceptionNotificationImpl
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class ProcessorMarketClient(
    private val exceptionNotification: DiscordExceptionNotificationImpl,
) : BaseClient(exceptionNotification) {

    private val baseUrl: String = "http://localhost:8080/api/v1/auctions/prices"

    val client: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun sendAuctionPriceData(itemCode: Int, listingPrices: List<AuctionListingPrice>) = coroutineScope {
        client.post()
            .uri(itemCode.toString())
            .bodyValue(listingPrices)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe("send_auction_price_data")
    }

    suspend fun deleteTodayPricesSnapshot() = coroutineScope {
        client.delete()
            .uri("market/items/snapshots/today")
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe()
    }
}