package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.AuctionUseCase
import io.oikkani.processorservice.infrastructure.outbound.repository.adapter.AuctionItemOhlcPriceRepositoryAdapter
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import org.springframework.stereotype.Service

@Service
class AuctionService(
    private val repository: AuctionItemOhlcPriceRepositoryAdapter
): AuctionUseCase{
    override fun getAllTodayItems(): List<DailyAuctionItemOhlcPrice> {
        return repository.getAllTodayItems()
    }

    override fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPrice> {
        return repository.findAllByItemCode(itemCode)
    }
}