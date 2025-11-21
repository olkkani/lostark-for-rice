package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.AuctionItemPriceUseCase
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorAuctionClient
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import org.springframework.stereotype.Service

@Service
class AuctionService(
    private val processorAuctionClient: ProcessorAuctionClient,
): AuctionItemPriceUseCase {
    override suspend fun getAllTodayItemsPreview(): List<ItemPreview> {
        return processorAuctionClient.getAllTodayItemPreview()
    }

    override suspend fun findOhlcPriceChartByItemCode(itemCode: Int): List<CandleChart> {
        return processorAuctionClient.findOhlcPriceChartByItemCode(itemCode)
    }
}