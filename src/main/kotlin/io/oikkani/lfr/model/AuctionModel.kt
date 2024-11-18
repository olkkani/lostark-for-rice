package io.oikkani.lfr.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionItem(
    val auctionInfo: AuctionInfo
)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionRequest(
    val itemTier: Long,
    val itemGrade: String,
    val itemName: String,
    val pageNo: Long = 0,
    val sortCondition: String = "ASC",
    val sort: String = "BUY_PRICE",
    val categoryCode: Long = 210000
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionResponse(
    val pageNo: Int,
    val pageSize: Int,
    val totalCount: Int,
    val items: List<AuctionItem>
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionInfo(
    val buyPrice: Long
)