package io.olkkani.lfr.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.lfr.domain.ItemPrices

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

