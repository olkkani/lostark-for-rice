package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.application.dto.DailyAuctionItemOhlcPriceDTO
import java.time.LocalDate


interface AuctionItemOhlcPriceRepositoryPort {
    fun save(ohlcPrice: DailyAuctionItemOhlcPriceDTO)
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyAuctionItemOhlcPriceDTO?
    fun findAllByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceDTO>
    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO>
}