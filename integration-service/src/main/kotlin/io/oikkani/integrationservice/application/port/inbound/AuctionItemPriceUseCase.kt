package io.oikkani.integrationservice.application.port.inbound

import io.oikkani.integrationservice.infrastructure.adapter.inbound.web.dto.ItemPreviewResponse
import io.olkkani.common.api.ChartResponse

interface AuctionItemPriceUseCase {
    fun getAllKindsTodayPrice(): List<ItemPreviewResponse>
    fun getPriceIndexByItemCode(itemCode: Int): List<ChartResponse>
}