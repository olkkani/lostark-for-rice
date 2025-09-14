package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.AuctionItemPriceUseCase
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import org.springframework.stereotype.Service

@Service
class AuctionItemPriceService: AuctionItemPriceUseCase {
    override fun getAllKindsTodayPrice(): List<ItemPreview> {
        TODO("Not yet implemented")
//        return listOf(ItemPreviewResponse())
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<CandleChart> {
        TODO("Not yet implemented")
    }
}