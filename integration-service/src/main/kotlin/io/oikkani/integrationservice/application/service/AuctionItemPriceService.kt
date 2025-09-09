package io.oikkani.integrationservice.application.service

import io.oikkani.integrationservice.application.port.inbound.AuctionItemPriceUseCase
import io.oikkani.integrationservice.infrastructure.adapter.inbound.web.dto.ItemPreviewResponse
import io.olkkani.common.api.ChartResponse
import org.springframework.stereotype.Service

@Service
class AuctionItemPriceService: AuctionItemPriceUseCase {
    override fun getAllKindsTodayPrice(): List<ItemPreviewResponse> {
        TODO("Not yet implemented")
//        return listOf(ItemPreviewResponse())
    }

    override fun getPriceIndexByItemCode(itemCode: Int): List<ChartResponse> {
        TODO("Not yet implemented")
    }
}