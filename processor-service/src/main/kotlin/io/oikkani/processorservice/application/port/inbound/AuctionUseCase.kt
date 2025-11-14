package io.oikkani.processorservice.application.port.inbound

import io.oikkani.processorservice.domain.model.DailyAuctionItemOhlcPriceDTO

interface AuctionUseCase {
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO>
    fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceDTO>
}