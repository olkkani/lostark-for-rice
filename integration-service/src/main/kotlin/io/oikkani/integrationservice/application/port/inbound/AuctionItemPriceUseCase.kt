package io.oikkani.integrationservice.application.port.inbound

import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart

interface AuctionItemPriceUseCase {
    fun getAllKindsTodayPrice(): List<ItemPreview>
    fun getPriceIndexByItemCode(itemCode: Int): List<CandleChart>
}