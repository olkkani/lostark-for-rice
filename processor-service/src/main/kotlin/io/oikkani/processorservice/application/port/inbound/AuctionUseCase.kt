package io.oikkani.processorservice.application.port.inbound

import io.oikkani.processorservice.application.dto.DailyAuctionItemOhlcPriceDTO

interface AuctionUseCase {
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO>
    fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceDTO>
}