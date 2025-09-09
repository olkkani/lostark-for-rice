package io.oikkani.processorservice.infrastructure.outbound.repository

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyAuctionItemOhlcPriceRepo: JpaRepository<DailyAuctionItemOhlcPrice, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<DailyAuctionItemOhlcPrice>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyAuctionItemOhlcPrice?
    fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyAuctionItemOhlcPrice>
}