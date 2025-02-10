package io.olkkani.lfr.dto

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



