package io.olkkani.common.dto.contract

data class CandleChart(
    val open: Int,
    val high: Int,
    val low: Int,
    val close: Int,
    val time: String
)