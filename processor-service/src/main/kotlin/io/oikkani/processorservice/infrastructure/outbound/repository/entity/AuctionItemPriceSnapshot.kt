package io.oikkani.processorservice.infrastructure.outbound.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.olkkani.common.dto.contract.AuctionPrice
import io.olkkani.common.dto.contract.AuctionPriceSnapshot
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "auction_item_price_snapshots")
class AuctionItemPriceSnapshot (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val endDate: LocalDateTime,
    val price: Int,
)

fun AuctionPriceSnapshot.toEntityList(): List<AuctionItemPriceSnapshot> = this.prices.map {
    AuctionItemPriceSnapshot(
        itemCode = this.itemCode,
        endDate = it.endDate,
        price = it.price
    )
}