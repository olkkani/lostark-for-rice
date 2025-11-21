package io.oikkani.integrationservice.domain.dto

import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.request.MarketRequest

class MarketItemCondition(
    val categoryCode: Int,
    val itemName: String? = null,
    val itemCode: Int? = null,
    val itemGrade: String? = null,
) {
    fun toFusionMaterialRequest() = MarketRequest(
        categoryCode = categoryCode,
        itemName = itemName,
    )
    fun toRelicEngravingRecipeRequest(pageNo: Int = 1) = MarketRequest(
        categoryCode = categoryCode,
        itemGrade = itemGrade,
        pageNo = pageNo,
    )
}