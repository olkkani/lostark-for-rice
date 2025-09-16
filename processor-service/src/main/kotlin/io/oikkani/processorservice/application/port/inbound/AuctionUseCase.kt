package io.oikkani.processorservice.application.port.inbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice

interface AuctionUseCase {
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPrice>
    fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPrice>
}