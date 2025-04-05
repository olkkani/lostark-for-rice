package io.olkkani.lfr.repository.jpa

import io.olkkani.lfr.entity.jpa.MarketPriceIndex
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MarketPriceIndexRepo: JpaRepository<MarketPriceIndex, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<MarketPriceIndex>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): MarketPriceIndex?
}