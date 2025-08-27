package io.oikkani.integrationservice.infrastructure.adapter.`in`.web.dto

data class CandleChartResponse(
    val open: Int,
    val high: Int,
    val low: Int,
    val close: Int,
    val time: String
)