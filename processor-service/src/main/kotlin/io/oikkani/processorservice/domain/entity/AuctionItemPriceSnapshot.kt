package io.oikkani.processorservice.domain.entity

import io.hypersistence.utils.hibernate.id.Tsid
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