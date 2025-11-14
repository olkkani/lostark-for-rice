package io.oikkani.processorservice.domain.model

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshotEntity
import io.olkkani.common.dto.contract.AuctionItemPrice
import java.time.LocalDateTime

class AuctionItemPriceSnapshotDTO(
    val id: Long? = null,
    val itemCode: Int,
    val endDate: LocalDateTime,
    val price: Int,
) {
    fun toEntity() = AuctionItemPriceSnapshotEntity(
        id = id,
        itemCode = itemCode,
        endDate = endDate,
        price = price,
    )
}

fun AuctionItemPrice.toSnapshots(): List<AuctionItemPriceSnapshotDTO> = this.prices.map {
    AuctionItemPriceSnapshotDTO(
        itemCode = this.itemCode,
        endDate = it.endDate,
        price = it.price
    )
}