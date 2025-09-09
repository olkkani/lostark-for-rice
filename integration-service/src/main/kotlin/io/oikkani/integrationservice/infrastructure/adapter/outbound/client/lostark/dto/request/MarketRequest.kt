package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.request

data class MarketRequest(
    val categoryCode: Int,
    val itemName: String? = null,
    val itemGrade: String? = null,
    var pageNo: Int = 1,
)

class MarketDTO (
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