package io.oikkani.integrationservice.infrastructure.adapter.`in`.web.dto

class ItemPreviewDTO(
    val itemCode: Int,
    val price: Int,
    val highPrice: Int,
    val lowPrice: Int,
    val priceChange: Int,
    val priceChangeRate: Double
)