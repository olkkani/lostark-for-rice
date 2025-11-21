package io.oikkani.integrationservice.domain.dto

class MarketListingPrice(
    val yesterdayAvgPrice: Int = 0,
    val price: Int,
    val endDate: String,
)