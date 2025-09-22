package io.oikkani.processorservice.infrastructure.outbound.repository.jooq

import org.jooq.DSLContext
import org.jooq.generated.Tables
import org.springframework.stereotype.Repository

@Repository
class DailyMarketItemOhlcaPriceJooqRepository(dsl: DSLContext) {
   private val table = Tables.DAILY_MARKET_ITEM_OHLCA_PRICES



}