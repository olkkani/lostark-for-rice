package io.oikkani.processorservice.infrastructure.outbound.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.oikkani.processorservice.domain.model.AuctionItemPriceSnapshotDTO
import io.olkkani.common.dto.contract.AuctionItemPrice
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "auction_item_price_snapshots")
class AuctionItemPriceSnapshotEntity (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val endDate: LocalDateTime,
    val price: Int,
){
    fun toDomain() = AuctionItemPriceSnapshotDTO(
        id = id,
        itemCode = itemCode,
        endDate = endDate,
        price = price
    )
}

fun AuctionItemPrice.toEntityList(): List<AuctionItemPriceSnapshotDTO> = this.prices.map {
    AuctionItemPriceSnapshotDTO(
        itemCode = this.itemCode,
        endDate = it.endDate,
        price = it.price
    )
}