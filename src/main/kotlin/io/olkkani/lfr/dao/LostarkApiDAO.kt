package io.olkkani.lfr.dao

import io.olkkani.lfr.dto.AuctionRequest
import io.olkkani.lfr.dto.MarketRequest


class MarketDAO(
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

class AuctionDAO(
    val categoryCode: Int = 210000,
    val sort: String = "BUY_PRICE",
    val sortCondition: String = "ASC",
    val itemName: String,
    val pageNo: Int = 0,
    val itemCode: Int,
    val pairItemCode: Int,
) {
    fun toGemRequest() = AuctionRequest(
        categoryCode = categoryCode,
        itemName = itemName,
        pageNo = pageNo,
        sort = sort,
        sortCondition = sortCondition,
    )
}