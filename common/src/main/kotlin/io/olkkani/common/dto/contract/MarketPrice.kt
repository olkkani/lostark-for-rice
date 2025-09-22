package io.olkkani.common.dto.contract

class MarketPrice (
    val itemCode: Int,
    val price: Int,
    val yDateAvgPrice: Float = 0F,
)