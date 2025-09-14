package io.oikkani.processorservice.domain.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice

interface MarketItemOhlcaRepositoryPort {
    fun save(ohlcPrice: DailyMarketItemOhlcaPrice)
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: String): DailyMarketItemOhlcaPrice?
}