package io.olkkani.lfr.dto

data class CandleChartResponse(
    val open: Int,
    val high: Int,
    val low: Int,
    val close: Int,
    val time: String
)