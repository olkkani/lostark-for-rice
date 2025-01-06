package io.olkkani.lfr.dto

import io.olkkani.lfr.domain.ItemPrices
import io.olkkani.lfr.model.Item

data class CandleChartResponse(
    val open: Int,
    val high: Int,
    val low: Int,
    val close: Int,
    val time: String
)
class TodayPriceResponse (
    val itemCode: Int,
    val price: CandleChartResponse
)

fun ItemPrices.toResponse() = CandleChartResponse(
    open = openPrice,
    high = highPrice,
    low = lowPrice,
    close = closePrice,
    time = recordedDate.toString()
)

fun Item.toResponse()  = CandleChartResponse(
    open = open,
    high = high,
    low = low,
    close = close,
    time = time.toString()
)
