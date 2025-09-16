package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice

interface MarketItemOhlcaRepositoryPort {
    fun save(ohlcPrice: DailyMarketItemOhlcaPrice)
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: String): DailyMarketItemOhlcaPrice?
}