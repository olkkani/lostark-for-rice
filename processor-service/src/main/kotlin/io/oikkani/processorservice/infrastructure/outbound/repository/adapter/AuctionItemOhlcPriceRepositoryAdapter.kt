package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.AuctionItemOhlcPriceRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.DailyAuctionItemOhlcPriceJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.DailyAuctionItemOhlcPriceJpaRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AuctionItemOhlcPriceRepositoryAdapter(
    private val jpaRepo: DailyAuctionItemOhlcPriceJpaRepository,
    private val jooqRepo: DailyAuctionItemOhlcPriceJooqRepository,
): AuctionItemOhlcPriceRepositoryPort {
    override fun save(ohlcPrice: DailyAuctionItemOhlcPrice) {
        jpaRepo.save(ohlcPrice)
    }

    override fun findByItemCodeAndRecordedDate(
        itemCode: Int,
        recordedDate: LocalDate
    ): DailyAuctionItemOhlcPrice? {
        return jpaRepo.findByItemCodeAndRecordedDate(itemCode, recordedDate)
    }

    override fun findAllByItemCode(itemCode: Int): List<DailyAuctionItemOhlcPrice> {
        return jpaRepo.findAllByItemCode(itemCode)
    }

    override fun getAllTodayItems(): List<DailyAuctionItemOhlcPrice> {
        return jooqRepo.getAllTodayItems()
    }

}