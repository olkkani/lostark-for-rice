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

val gemsInfo = listOf(
    65021100 to AuctionRequest(itemName = "10레벨 멸화의 보석"),
    65022100 to AuctionRequest(itemName = "10레벨 홍염의 보석"),
    65031080 to AuctionRequest(itemName = "8레벨 겁화의 보석"),
    65032080 to AuctionRequest(itemName = "8레벨 작열의 보석"),
    65031100 to AuctionRequest(itemName = "10레벨 겁화의 보석"),
    65032100 to AuctionRequest(itemName = "10레벨 작열의 보석" )
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