package io.oikkani.processorservice.application.port.inbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity

interface AuctionUseCase {
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceEntity>
    fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceEntity>
}