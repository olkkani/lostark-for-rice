package io.olkkani.lfr.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionItem(
    val auctionInfo: AuctionInfo
)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionRequest(
    val itemTier: Int,
    val itemGrade: String,
    val itemName: String,
    val itemCode: Int,
    val pageNo: Int = 0,
    val sortCondition: String = "ASC",
    val sort: String = "BUY_PRICE",
    val categoryCode: Int = 210000
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
    val buyPrice: Int
)


val gemsInfo = listOf(
    "lv10Annihilation" to AuctionRequest(itemTier = 3, itemGrade = "유물", itemCode = 65021100, itemName = "10레벨 멸화의 보석"),
    "lv10CrimsonFlame" to AuctionRequest(itemTier = 3, itemGrade = "유물", itemCode = 65022100, itemName = "10레벨 홍염의 보석"),
    "lv8DoomFire" to AuctionRequest(itemTier = 4, itemGrade = "유물", itemCode = 65031080, itemName = "8레벨 겁화의 보석"),
    "lv8Blazing" to AuctionRequest(itemTier = 4, itemGrade = "유물", itemCode = 65032080, itemName = "8레벨 작열의 보석"),
    "lv10DoomFire" to AuctionRequest(itemTier = 4, itemGrade = "고대", itemCode = 65031100, itemName = "10레벨 겁화의 보석"),
    "lv10Blazing" to AuctionRequest(itemTier = 4, itemGrade = "고대", itemCode = 65032100, itemName = "10레벨 작열의 보석")
)
