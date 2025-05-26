package io.olkkani.lfr.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "daily_market_item_ohlca_prices")
class DailyMarketItemOhlcaPrice (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int?,
    val recordedDate: LocalDate,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
    var closePrice: Int,
    var avgPrice: Float = 0F,
)
