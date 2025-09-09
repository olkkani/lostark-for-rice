package io.olkkani.common.api

class ItemPreviewResponse(
    val itemCode: Int,
    val price: Int,
    val highPrice: Int,
    val lowPrice: Int,
    val priceChange: Int,
    val priceChangeRate: Double
)