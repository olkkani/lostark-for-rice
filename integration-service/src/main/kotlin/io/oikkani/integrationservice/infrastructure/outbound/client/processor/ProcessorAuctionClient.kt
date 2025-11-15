package io.oikkani.integrationservice.infrastructure.outbound.client.processor

import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.outbound.client.BaseClient
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.AuctionItemPrice
import io.olkkani.common.dto.contract.CandleChart
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient


@Component
class ProcessorAuctionClient(
    @param:Value("\${service.domain}") private val serviceDomain: String,
    private val exceptionNotification: ExceptionNotification,
) : BaseClient(exceptionNotification) {

    val client: WebClient = WebClient.builder()
        .baseUrl(serviceDomain)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .build()

    suspend fun saveAuctionPriceData(request: AuctionItemPrice) {
        client.post()
            .uri("auction/items/snapshots")
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .withCommonRetry()
            .awaitSingle()
    }

    suspend fun getAllTodayItemPreview(): List<ItemPreview> {
        return client.get()
            .uri("auction/items/preview/today")
            .retrieve()
            .bodyToFlux(ItemPreview::class.java)
            .withCommonRetry()
            .collectList()
            .awaitSingle()
    }

    suspend fun findOhlcPriceChartByItemCode(itemCode: Int): List<CandleChart> {
        return client.get()
            .uri("auction/items/$itemCode/ohlc")
            .retrieve()
            .bodyToFlux(CandleChart::class.java)
            .withCommonRetry()
            .collectList()
            .awaitSingle()
    }

    suspend fun deleteTodayPricesSnapshot() {
        client.delete()
            .uri("auction/items/snapshots")
            .retrieve()
            .toBodilessEntity()
            .withCommonRetry()
            .awaitSingle()
    }
}