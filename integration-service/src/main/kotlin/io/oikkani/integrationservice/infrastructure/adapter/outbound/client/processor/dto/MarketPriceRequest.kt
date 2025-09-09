package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto

class MarketPriceRequest(
    val itemCode: Int,
    val price: Int,
    val yDatePrice: Int,
    val isOpeningJob: Boolean = false,
)