package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto

class MarketPriceRequest(
    val itemCode: Int,
    val price: Int,
    val yesterdayAvgPrice: Int = 0,
    val isOpeningJob: Boolean = false,
)