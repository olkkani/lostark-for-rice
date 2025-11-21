package io.oikkani.processorservice.infrastructure.outbound.repository.entity

import io.hypersistence.utils.hibernate.id.Tsid
import io.oikkani.processorservice.application.dto.ItemPreviousPriceChangeDTO
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "item_previous_price_changes")
class ItemPreviousPriceChangeEntity (
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var price: Int,

    val priceDiffPrevDay: Int,
    val priceDiffRatePrevDay: Double,

    val priceDiffPairItem: Int? = null,
    val priceDiffRatePairItem: Double? = null,
){
    fun toDomain() = ItemPreviousPriceChangeDTO(
        id = id,
        itemCode = itemCode,
        recordedDate = recordedDate,
        price = price,
        priceDiffPrevDay = priceDiffPrevDay,
        priceDiffRatePrevDay = priceDiffRatePrevDay,
        priceDiffPairItem = priceDiffPairItem,
        priceDiffRatePairItem = priceDiffRatePairItem,
    )
    // todo
//    fun toResponse() = ItemPreviousChangeResponse(
//        recordedDate = recordedDate,
//        price = price,
//        priceDiffPrevDay = priceDiffPrevDay,
//        priceDiffRatePrevDay = priceDiffRatePrevDay,
//        priceDiffPairItem = priceDiffPairItem,
//        priceDiffRatePairItem = priceDiffRatePairItem,
//    )
}