package io.oikkani.integrationservice.infrastructure.adapter.inbound.web.dto

class ItemPreviewResponse(
    val itemCode: Int,
    val price: Int,
    val highPrice: Int,
    val lowPrice: Int,
    val priceChange: Int,
    val priceChangeRate: Double
)