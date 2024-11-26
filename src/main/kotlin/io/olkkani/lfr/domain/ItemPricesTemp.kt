package io.olkkani.lfr.domain

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class ItemPricesTemp(
    @Id @Tsid
    val id: Long?= null,
    @Column
    val recordedDate: LocalDate,
    @Column
    val itemCode: Int,
    @Column
    val price: Int
)