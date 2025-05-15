package io.olkkani.lfr.repository.jpa

import io.olkkani.lfr.entity.jpa.MarketItemOhlcPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MarketPriceIndexRepo: JpaRepository<MarketItemOhlcPrice, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<MarketItemOhlcPrice>
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): MarketItemOhlcPrice?
}