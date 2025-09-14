package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.domain.outbound.AuctionItemOhlcPriceRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPrice
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemOhlcPriceJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class AuctionItemOhlcPriceRepositoryAdapter(
    private val jpaRepo: AuctionItemOhlcPriceJpaRepository,
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

}