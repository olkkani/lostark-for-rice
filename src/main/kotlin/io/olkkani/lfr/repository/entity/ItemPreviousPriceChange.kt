package io.olkkani.lfr.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.olkkani.lfr.controller.dto.ItemPreviousChangeResponse
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "item_previous_price_changes")
class ItemPreviousPriceChange (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var price: Int,

    var priceDiffPrevDay: Int,
    var priceDiffRatePrevDay: Double,

    var priceDiffPairItem: Int? = null,
    var priceDiffRatePairItem: Double? = null,
){
    fun toResponse() = ItemPreviousChangeResponse(
        recordedDate = recordedDate,
        price = price,
        priceDiffPrevDay = priceDiffPrevDay,
        priceDiffRatePrevDay = priceDiffRatePrevDay,
        priceDiffPairItem = priceDiffPairItem,
        priceDiffRatePairItem = priceDiffRatePairItem,
    )
}