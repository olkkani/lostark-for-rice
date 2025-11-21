package io.oikkani.processorservice.application.dto

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshotEntity
import io.olkkani.common.dto.contract.MarketItemPrice

class MarketItemPriceSnapshotDTO(
    val id: Long? = null,
    val itemCode: Int,
    val price: Int,
) {
    fun toEntity() = MarketItemPriceSnapshotEntity(
        id = id,
        itemCode = itemCode,
        price = price
    )
}

fun MarketItemPrice.toSnapshot() = MarketItemPriceSnapshotDTO(
    itemCode = this.itemCode,
    price = this.price
)