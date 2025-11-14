package io.oikkani.processorservice.domain.model

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.ItemPreviousPriceChangeEntity
import java.time.LocalDate

class ItemPreviousPriceChangeDTO(
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var price: Int,

    var priceDiffPrevDay: Int,
    var priceDiffRatePrevDay: Double,
    var priceDiffPairItem: Int? = null,
    var priceDiffRatePairItem: Double? = null,
) {
    fun toEntity() = ItemPreviousPriceChangeEntity(
        id = id,
        itemCode = itemCode,
        recordedDate = recordedDate,
        price = price,
        priceDiffPrevDay = priceDiffPrevDay,
        priceDiffRatePrevDay = priceDiffRatePrevDay,
        priceDiffPairItem = priceDiffPairItem,
        priceDiffRatePairItem = priceDiffRatePairItem,
    )
}