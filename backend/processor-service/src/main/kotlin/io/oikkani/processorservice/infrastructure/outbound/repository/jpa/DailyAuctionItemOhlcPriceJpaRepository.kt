package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyAuctionItemOhlcPriceJpaRepository: JpaRepository<DailyAuctionItemOhlcPriceEntity, Long> {
    fun findAllByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceEntity>
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<DailyAuctionItemOhlcPriceEntity>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyAuctionItemOhlcPriceEntity?
    fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyAuctionItemOhlcPriceEntity>
}