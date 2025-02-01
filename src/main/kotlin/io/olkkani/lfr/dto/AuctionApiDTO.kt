package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionRequest(
    val itemName: String,
    val categoryCode: Int = 210000,
    val pageNo: Int = 0,
    val sort: String = "BUY_PRICE",
    val sortCondition: String = "ASC",
)

class GemInfo (
    val itemCode: Int,
    val pairItemCode: Int,
    val request: AuctionRequest
)

val collectGemInfoList: List<GemInfo> = listOf(
    GemInfo(itemCode =65021100, pairItemCode = 65022100, request = AuctionRequest(itemName = "10레벨 멸화의 보석")),
    GemInfo(itemCode = 65022100, pairItemCode = 65021100, request = AuctionRequest(itemName = "10레벨 홍염의 보석")),
    GemInfo(itemCode = 65031080, pairItemCode = 65032080, request = AuctionRequest(itemName = "8레벨 겁화의 보석")),
    GemInfo(itemCode = 65032080, pairItemCode = 65031080, request = AuctionRequest(itemName = "8레벨 작열의 보석")),
    GemInfo(itemCode = 65031100, pairItemCode = 65032100, request = AuctionRequest(itemName = "10레벨 겁화의 보석")),
    GemInfo(itemCode = 65032100, pairItemCode = 65031100, request = AuctionRequest(itemName = "10레벨 작열의 보석"))
)

data class AuctionResponse(
    @JsonProperty("Items")val items: List<AuctionItem>
)
data class AuctionItem(
    @JsonProperty("AuctionInfo")val auctionInfo: AuctionInfo,
)
data class AuctionInfo(
    @JsonProperty("BuyPrice")val buyPrice: Int,
    @JsonProperty("EndDate")val endDate: LocalDateTime
)