package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.common.dto.contract.MarketPrice


@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class MarketResponse (
    val items: List<Item>
){
    fun toDomain() = items.map {
        MarketPrice(
            itemCode = it.id,
            price = it.currentMinPrice,
            yDateAvgPrice = it.yDayAvgPrice,
        )
    }
}
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Item (
    val id: Int,
    val currentMinPrice: Int,
    val yDayAvgPrice: Float,
)