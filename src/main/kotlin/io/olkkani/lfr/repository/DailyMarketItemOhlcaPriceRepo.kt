package io.olkkani.lfr.repository

import io.olkkani.lfr.repository.entity.DailyMarketItemOhlcaPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyMarketItemOhlcaPriceRepo: JpaRepository<DailyMarketItemOhlcaPrice, Long> {
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyMarketItemOhlcaPrice?
}