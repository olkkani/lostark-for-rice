package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.application.dto.DailyMarketItemOhlcaPriceDTO
import java.time.LocalDate

interface MarketItemOhlcaRepositoryPort {
    fun save(ohlcPrice: DailyMarketItemOhlcaPriceDTO)
    fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyMarketItemOhlcaPriceDTO>
}