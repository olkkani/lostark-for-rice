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

class ItemPreview(
    val price: Int,
    val highPrice: Int,
    val lowPrice: Int,
    val priceChange: Int,
    val priceChangeRate: Double
)

class TodayPriceResponse (
    val itemCode: Int,
    val price: ItemPreview
)

fun ItemPrices.toResponse() = CandleChartResponse(
    open = openPrice,
    high = highPrice,
    low = lowPrice,
    close = closePrice,
    time = recordedDate.toString()
)

fun Item.toResponse() = ItemPreview(
    price = close,
    highPrice = high,
    lowPrice = low,
    priceChange = close - open,
    priceChangeRate = close.toDouble() / open.toDouble(),
)