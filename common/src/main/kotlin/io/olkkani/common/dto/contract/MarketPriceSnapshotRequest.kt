package io.olkkani.common.dto.contract

class MarketPriceSnapshotRequest(
    val isUpdateYesterdayAvgPrice: Boolean = false,
    val prices: List<MarketItemPrice>
)
