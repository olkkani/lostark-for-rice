package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.AuctionUseCase
import io.oikkani.processorservice.application.dto.DailyAuctionItemOhlcPriceDTO
import io.oikkani.processorservice.infrastructure.outbound.repository.adapter.AuctionItemOhlcPriceRepositoryAdapter
import org.springframework.stereotype.Service

@Service
class AuctionService(
    private val repository: AuctionItemOhlcPriceRepositoryAdapter
): AuctionUseCase{
    override fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO> {
        return repository.getAllTodayItems()
    }

    override fun findAllOhlcByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceDTO> {
        return repository.findAllByItemCode(itemCode)
    }
}