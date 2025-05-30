package io.olkkani.lfr.controller.dto

class ItemPreviewDTO(
    val itemCode: Int,
    val price: Int,
    val highPrice: Int,
    val lowPrice: Int,
    val priceChange: Int,
    val priceChangeRate: Double
)