package io.oikkani.processorservice.infrastructure.outbound.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.oikkani.processorservice.domain.model.MarketItemPriceSnapshotDTO
import io.olkkani.common.dto.contract.MarketItemPrice
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "market_item_price_snapshots")
class MarketItemPriceSnapshotEntity(
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val price: Int,
){
    fun toDomain() = MarketItemPriceSnapshotDTO(
        id = id,
        itemCode = itemCode,
        price = price
    )
}

fun MarketItemPrice.toEntity() = MarketItemPriceSnapshotEntity(
    itemCode = this.itemCode,
    price = this.price
)