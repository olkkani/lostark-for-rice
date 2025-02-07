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
    @Column
    val itemCode: Int? = null,
    @Column
    val recordedDate: LocalDate,
    @Column
    var closePrice: Int,
    @Column
    var openPrice: Int,
    @Column
    var highPrice: Int,
    @Column
    var lowPrice: Int,
)

