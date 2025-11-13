package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.MarketItemOhlcaRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyMarketItemOhlcaPrice
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.DailyMarketItemOhlcaPriceJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyMarketItemOhlcaPriceJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MarketItemOhlcaRepositoryAdapter(
    private val jpaRepository: DailyMarketItemOhlcaPriceJpaRepository,
    private val jooqRepository: DailyMarketItemOhlcaPriceJooqRepository
): MarketItemOhlcaRepositoryPort {
    override fun save(ohlcPrice: DailyMarketItemOhlcaPrice) {
        jpaRepository.save(ohlcPrice)
    }

    override fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyMarketItemOhlcaPrice> {
        return jpaRepository.findAllByRecordedDate(recordedDate)
    }
}