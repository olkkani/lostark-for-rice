package io.olkkani.lfr.entity.jpa

import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
class ItemPriceIndex (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int? = null,
    val recordedDate: LocalDate,
    var closePrice: Int,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
)

