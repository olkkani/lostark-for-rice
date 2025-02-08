package io.olkkani.lfr.dto

data class ItemTodayPriceDTO (
    val itemCode: Int,
    val price: Int,
    val priceGap: Int,
    val priceGapRate: Double,
)