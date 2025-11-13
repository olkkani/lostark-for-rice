package io.oikkani.integrationservice.application.port.inbound

import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart

interface AuctionItemPriceUseCase {
    suspend fun getAllTodayItemsPreview(): List<ItemPreview>
    suspend fun findOhlcPriceChartByItemCode(itemCode: Int): List<CandleChart>
}