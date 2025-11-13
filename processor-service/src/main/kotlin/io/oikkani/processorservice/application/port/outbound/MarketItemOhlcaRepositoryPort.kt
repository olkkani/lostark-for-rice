package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice
import java.time.LocalDate

interface MarketItemOhlcaRepositoryPort {
    fun save(ohlcPrice: DailyMarketItemOhlcaPrice)
    fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyMarketItemOhlcaPrice>
}