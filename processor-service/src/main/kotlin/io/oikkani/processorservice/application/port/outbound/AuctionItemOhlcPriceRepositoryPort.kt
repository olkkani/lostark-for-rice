package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity
import java.time.LocalDate


interface AuctionItemOhlcPriceRepositoryPort {
    fun save(ohlcPrice: DailyAuctionItemOhlcPriceEntity)
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyAuctionItemOhlcPriceEntity?
    fun findAllByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceEntity>
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceEntity>
}