package io.oikkani.processorservice.infrastructure.outbound.repository.jooq

import io.oikkani.processorservice.domain.model.DailyAuctionItemOhlcPriceDTO
import org.jooq.DSLContext
import org.jooq.generated.Tables
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class DailyAuctionItemOhlcPriceJooqRepository(private val dsl: DSLContext) {

    private val table = Tables.DAILY_AUCTION_ITEM_OHLC_PRICES

    fun getAllTodayItems(): List<DailyAuctionItemOhlcPriceDTO>{
        return dsl.selectFrom(table)
            .where(table.RECORDED_DATE.eq(LocalDate.now()))
            .fetchInto(DailyAuctionItemOhlcPriceDTO::class.java)
    }
}