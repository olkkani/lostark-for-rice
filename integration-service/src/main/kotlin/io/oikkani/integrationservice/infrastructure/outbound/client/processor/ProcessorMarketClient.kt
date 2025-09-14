package io.oikkani.integrationservice.infrastructure.outbound.client.processor

import io.olkkani.common.dto.contract.AuctionPrice
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.BaseClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.notofication.DiscordExceptionNotificationImpl
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ProcessorMarketClient(
    @Value("\${processor.url:must-not-null-processor-url}") processorServiceUrl: String,
    private val exceptionNotification: DiscordExceptionNotificationImpl,
) : BaseClient(exceptionNotification) {

    val client: WebClient = WebClient.builder()
        .baseUrl(processorServiceUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun sendMarketPriceData(itemCode: Int, listingPrices: List<AuctionPrice>) = coroutineScope {
        client.post()
            .uri("market/items/snapshots/$itemCode")
            .bodyValue(listingPrices)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe("send_market_price_data")
    }

    suspend fun deleteTodayPricesSnapshot() = coroutineScope {
        client.delete()
            .uri("market/items/snapshots/today")
            .retrieve()
            .toBodilessEntity()
            .withCommonRetryAndSubscribe()
    }
}