package io.olkkani.lfr.entity.jpa

import io.hypersistence.utils.hibernate.id.Tsid
import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.ItemPreviewDTO
import io.olkkani.lfr.util.PercentageCalculation
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "daily_auction_item_ohlc")
class AuctionItemOhlcPrice(
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
    var closePrice: Int,
){
    fun toPreviewResponse() = ItemPreviewDTO(
        itemCode = itemCode,
        price = closePrice,
        highPrice = highPrice,
        lowPrice = lowPrice,
        priceChange = closePrice - openPrice,
        priceChangeRate = PercentageCalculation().calc(closePrice, openPrice),
    )
    fun toChartResponse() = CandleChartResponse(
        open = openPrice,
        high = highPrice,
        low = lowPrice,
        close = closePrice,
        time = recordedDate.toString()
    )
}