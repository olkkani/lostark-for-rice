package io.olkkani.lfr.repository.jpa

import io.olkkani.lfr.entity.jpa.AuctionItemOhlcPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AuctionPriceIndexRepo: JpaRepository<AuctionItemOhlcPrice, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<AuctionItemOhlcPrice>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): AuctionItemOhlcPrice?
    fun findAllByRecordedDate(recordedDate: LocalDate): List<AuctionItemOhlcPrice>
}