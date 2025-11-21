package io.oikkani.integrationservice.infrastructure.outbound.client.processor.dto

class MarketPriceRequest(
    val itemCode: Int,
    val price: Int,
    val yesterdayAvgPrice: Int = 0,
    val isOpeningJob: Boolean = false,
)