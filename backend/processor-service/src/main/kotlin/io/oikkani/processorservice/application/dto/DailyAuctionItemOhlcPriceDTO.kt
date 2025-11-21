package io.oikkani.processorservice.application.dto

import io.oikkani.processorservice.application.util.PercentageCalculation
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.DailyAuctionItemOhlcPriceEntity
import io.olkkani.common.api.ItemPreview
import io.olkkani.common.dto.contract.CandleChart
import java.time.LocalDate

data class DailyAuctionItemOhlcPriceDTO(
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
    var closePrice: Int,
){
    fun toEntity() = DailyAuctionItemOhlcPriceEntity(
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
