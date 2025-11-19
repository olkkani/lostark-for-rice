package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.MarketItemOhlcaRepositoryPort
import io.oikkani.processorservice.application.dto.DailyMarketItemOhlcaPriceDTO
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyMarketItemOhlcaPriceJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MarketItemOhlcaRepositoryAdapter(
    private val jpaRepository: DailyMarketItemOhlcaPriceJpaRepository,
): MarketItemOhlcaRepositoryPort {
    override fun save(ohlcPrice: DailyMarketItemOhlcaPriceDTO) {
        jpaRepository.save(ohlcPrice.toEntity())
    }

    override fun findAllByRecordedDate(recordedDate: LocalDate): List<DailyMarketItemOhlcaPriceDTO> {
        return jpaRepository.findAllByRecordedDate(recordedDate).map { it.toDomain() }
    }
}