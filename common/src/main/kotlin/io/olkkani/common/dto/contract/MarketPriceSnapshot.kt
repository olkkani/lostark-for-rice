package io.olkkani.common.dto.contract

class MarketPriceSnapshot(
    val isUpdateYesterdayAvgPrice: Boolean = false,
    val prices: List<MarketPrice>
)