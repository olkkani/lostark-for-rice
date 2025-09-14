package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming


@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class MarketResponse (
    val items: List<Item>
)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class Item (
    val id: String,
    val currentMinPrice: Int,
    val yDayAvgPrice: Float,
)