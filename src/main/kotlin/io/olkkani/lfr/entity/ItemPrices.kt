package io.olkkani.lfr.entity

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class ItemPrices (
    @Id @Tsid
    val id: Long? = null,
    @Column
    val itemCode: Int? = null,
    @Column
    val closePrice: Int,
    @Column
    val openPrice: Int,
    @Column
    val highPrice: Int,
    @Column
    val lowPrice: Int,
    @Column
    val recordedDate: LocalDate?,
)

