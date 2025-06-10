package io.olkkani.lfr.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "market_item_price_snapshots")
class MarketItemPriceSnapshot(
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val price: Int,
)