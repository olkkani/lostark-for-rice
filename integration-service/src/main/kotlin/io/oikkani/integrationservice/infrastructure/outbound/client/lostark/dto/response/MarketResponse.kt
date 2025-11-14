package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.common.dto.contract.MarketItemPrice
import io.olkkani.common.dto.contract.MarketPriceSnapshotRequest


@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class MarketResponse(
    val items: List<Item>
) {
    fun toSnapshotRequest(isUpdateYesterdayAvgPrice: Boolean) = MarketPriceSnapshotRequest(
        isUpdateYesterdayAvgPrice = isUpdateYesterdayAvgPrice,
        prices = items.map {
            MarketItemPrice(
                itemCode = it.id,
                price = it.currentMinPrice,
                yDateAvgPrice = it.yDayAvgPrice,
            )
        }
    )
    fun extractPrices() = items.map {
        MarketItemPrice(
            itemCode = it.id,
            price = it.currentMinPrice,
            yDateAvgPrice = it.yDayAvgPrice,
        )
    }
}

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Item(
    val id: Int,
    val currentMinPrice: Int,
    val yDayAvgPrice: Float,
)