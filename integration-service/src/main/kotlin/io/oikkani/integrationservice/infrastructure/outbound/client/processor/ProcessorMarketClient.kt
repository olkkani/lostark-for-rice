package io.oikkani.integrationservice.infrastructure.outbound.client.processor

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.outbound.client.BaseClient
import io.olkkani.common.dto.contract.MarketPrice
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ProcessorMarketClient(
    @Value("\${processor.url:must-not-null-processor-url}") processorServiceUrl: String,
    private val exceptionNotification: ExceptionNotification,
) : BaseClient(exceptionNotification) {

    val client: WebClient = WebClient.builder()
        .baseUrl(processorServiceUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun sendMarketPriceData(marketPrices: List<MarketPrice>) {
        client.post()
            .uri("market/items/snapshots")
            .bodyValue(marketPrices)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetry()
            .awaitSingle()
    }

    suspend fun deleteTodayPricesSnapshot() {
        client.delete()
            .uri("market/items/snapshots")
            .retrieve()
            .toBodilessEntity()
            .withCommonRetry()
            .awaitSingle()
    }
}