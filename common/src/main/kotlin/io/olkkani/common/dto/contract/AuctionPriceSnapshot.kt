package io.olkkani.common.dto.contract

class AuctionPriceSnapshot(
    val itemCode: Int,
    val prices: List<AuctionPrice>,
    val isUpdateOpenPrice: Boolean = false,
)