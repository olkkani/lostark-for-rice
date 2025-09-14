package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MarketItemOhlcaPriceJpaRepository: JpaRepository<DailyMarketItemOhlcaPrice, Long> {
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): DailyMarketItemOhlcaPrice?
}