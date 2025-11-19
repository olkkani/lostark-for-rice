package io.oikkani.processorservice.infrastructure.outbound.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.oikkani.processorservice.application.util.PercentageCalculation
import io.oikkani.processorservice.application.dto.DailyAuctionItemOhlcPriceDTO
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "daily_auction_item_ohlc_prices")
class DailyAuctionItemOhlcPriceEntity(
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
    var closePrice: Int,
){
    fun toDomain() = DailyAuctionItemOhlcPriceDTO(
        id = id,
        itemCode = itemCode,
        recordedDate = recordedDate,
        openPrice = openPrice,
        highPrice = highPrice,
        lowPrice = lowPrice,
        closePrice = closePrice,
    )
    fun toPreview() = ItemPreview(
        itemCode = itemCode,
        price = closePrice,
        highPrice = highPrice,
        lowPrice = lowPrice,
        priceChange = closePrice - openPrice,
        priceChangeRate = PercentageCalculation().calc(closePrice, openPrice),
    )

    fun toChart() = CandleChart(
        open = openPrice,
        high = highPrice,
        low = lowPrice,
        close = closePrice,
        time = recordedDate.toString()
    )
}