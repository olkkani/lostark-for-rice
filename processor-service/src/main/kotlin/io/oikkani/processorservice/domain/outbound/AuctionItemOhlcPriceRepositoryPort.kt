package io.oikkani.processorservice.domain.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import java.time.LocalDate


interface AuctionItemOhlcPriceRepositoryPort {
    fun save(ohlcPrice: DailyAuctionItemOhlcPrice)
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyAuctionItemOhlcPrice?
}