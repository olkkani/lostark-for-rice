package io.olkkani.lfr.repository.jpa

import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AuctionPriceIndexRepo: JpaRepository<AuctionPriceIndex, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<AuctionPriceIndex>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): AuctionPriceIndex?
    fun findAllByRecordedDate(recordedDate: LocalDate): List<AuctionPriceIndex>
}