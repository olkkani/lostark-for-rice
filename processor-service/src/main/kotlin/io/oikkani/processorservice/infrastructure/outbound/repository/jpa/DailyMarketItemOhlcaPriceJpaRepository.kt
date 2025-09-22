package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyMarketItemOhlcaPriceJpaRepository: JpaRepository<DailyMarketItemOhlcaPrice, Long> {
    fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyMarketItemOhlcaPrice>
}