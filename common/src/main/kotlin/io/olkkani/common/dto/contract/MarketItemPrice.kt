package io.olkkani.common.dto.contract

class MarketItemPrice (
    val itemCode: Int,
    val price: Int,
    val yDateAvgPrice: Float = 0F,
)