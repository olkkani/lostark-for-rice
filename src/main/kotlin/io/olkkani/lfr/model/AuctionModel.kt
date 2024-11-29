package io.olkkani.lfr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.lfr.domain.ItemPrices

//@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
//data class AuctionResponse(
//    val pageNo: Int,
//    val pageSize: Int,
//    val totalCount: Int,
//    val items: MutableList<AuctionItem> = mutableListOf()
//)
//
//@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
//data class AuctionItem(
//    val name: String,
//    val grade: String,
//    val tier: Int,
//    val level: Int,
//    val icon: String,
//    val gradeQuality: Int?,
//    val auctionInfo: AuctionInfo,
//    val options: List<AuctionOption>
//)
//
//@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
//data class AuctionInfo(
//    val startPrice: Int,
//    val buyPrice: Int,
//    val bidPrice: Int,
//    val endDate: String?,
//    val bidCount: Int,
//    val bidStartPrice: Int,
//    val isCompetitive: Boolean,
//    val tradeAllowCount: Int,
//    val upgradeLevel: Int?
//)
//
//@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
//data class AuctionOption(
//    val type: String,
//    val optionName: String,
//    val optionNameTripod: String?,
//    val value: Double,
//    val isPenalty: Boolean,
//    val className: String,
//    val isValuePercentage: Boolean
//)
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
data class ResponseData(
    @JsonProperty("Items") val items: List<Item>
)

data class Item(
    @JsonProperty("AuctionInfo") val auctionInfo: AuctionInfo
)

data class AuctionInfo(
    @JsonProperty("BuyPrice") val buyPrice: Int
)


//@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
//data class AuctionResponse(
//    val pageNo: Int,
//    val pageSize: Int,
//    val totalCount: Int,
//    val items: MutableList<AuctionItem> = mutableListOf(),
//)
//@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
//data class AuctionItem(
//    val auctionInfo: AuctionInfo
//)
//@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
//data class AuctionInfo(
//    val buyPrice: Int
//)


val gemsInfo = listOf(
    65021100 to AuctionRequest(itemName = "10레벨 멸화의 보석", itemTier = 3, itemGrade = "유물", itemCode = 65021100),
    65022100 to AuctionRequest(itemName = "10레벨 홍염의 보석", itemTier = 3, itemGrade = "유물", itemCode = 65022100),
    65031080 to AuctionRequest(itemName = "8레벨 겁화의 보석", itemTier = 4, itemGrade = "유물", itemCode = 65031080),
    65032080 to AuctionRequest(itemName = "8레벨 작열의 보석", itemTier = 4, itemGrade = "유물", itemCode = 65032080),
    65031100 to AuctionRequest(itemName = "10레벨 겁화의 보석", itemTier = 4, itemGrade = "고대", itemCode = 65031100),
    65032100 to AuctionRequest(itemName = "8레벨 작열의 보석", itemTier = 4, itemGrade = "고대", itemCode = 65032100 )
)

data class CandleChartResponse(
    val open: Int,
    val high: Int,
    val low: Int,
    val close: Int,
    val time: String
)

fun ItemPrices.toResponse() = CandleChartResponse(
    open = openPrice,
    high = highPrice,
    low = lowPrice,
    close = closePrice,
    time = recordedDate.toString()
)

