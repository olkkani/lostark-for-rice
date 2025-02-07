package io.olkkani.lfr.entity.jpa

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "item_price_index")
class ItemPriceIndex (
    @Id @Tsid
    val id: Long? = null,
    @Column(name = "item_code")
    val itemCode: Int? = null,
    @Column(name = "recorded_date")
    val recordedDate: LocalDate,
    @Column(name = "close_price")
    var closePrice: Int,
    @Column(name = "open_price")
    var openPrice: Int,
    @Column(name = "high_price")
    var highPrice: Int,
    @Column(name = "low_price")
    var lowPrice: Int,
)

