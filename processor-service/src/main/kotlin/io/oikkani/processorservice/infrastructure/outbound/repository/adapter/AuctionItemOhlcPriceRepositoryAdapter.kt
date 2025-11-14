package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.AuctionItemOhlcPriceRepositoryPort
import io.oikkani.processorservice.domain.model.DailyAuctionItemOhlcPriceDTO
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.DailyAuctionItemOhlcPriceJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyAuctionItemOhlcPriceJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AuctionItemOhlcPriceRepositoryAdapter(
    private val jpaRepo: DailyAuctionItemOhlcPriceJpaRepository,
    private val jooqRepo: DailyAuctionItemOhlcPriceJooqRepository,
): AuctionItemOhlcPriceRepositoryPort {
    override fun save(ohlcPrice: DailyAuctionItemOhlcPriceDTO) {
        jpaRepo.save(ohlcPrice.toEntity())
    }

    override fun findByItemCodeAndRecordedDate(
        itemCode: Int,
        recordedDate: LocalDate
    ): DailyAuctionItemOhlcPriceDTO? {
        return jpaRepo.findByItemCodeAndRecordedDate(itemCode, recordedDate)?.toDomain()
    }

    override fun findAllByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPriceDTO> {
        return jpaRepo.findAllByItemCode(itemCode).map { it.toDomain() }
    }

    override fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO> {
        return jooqRepo.getAllTodayItems()
    }

}