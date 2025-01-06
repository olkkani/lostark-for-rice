package io.olkkani.lfr.domain

import io.hypersistence.utils.hibernate.id.Tsid
import io.olkkani.lfr.model.Item
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class ItemPricesTemp(
    @Id @Tsid
    val id: Long? = null,
    @Column
    val recordedDate: LocalDate,
    @Column
    val endDate: LocalDateTime,
    @Column
    val itemCode: Int,
    @Column
    val price: Int
)

fun Item.toTempDomains(): MutableList<ItemPricesTemp> {
    val itemPricesTemps = mutableListOf<ItemPricesTemp>()
    todayPrices.forEach { item ->
        ItemPricesTemp(
            itemCode = itemCode, recordedDate = time, endDate = item.key, price = item.value
        ).also {
            itemPricesTemps.add(it)
        }
    }
    return itemPricesTemps
}