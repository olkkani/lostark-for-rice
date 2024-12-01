package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.olkkani.lfr.domain.ItemPrices






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

data class ResponseData(
    @JsonProperty("Items") val items: List<Item>
)

data class Item(
    @JsonProperty("AuctionInfo") val auctionInfo: AuctionInfo
)

//data class AuctionInfo(
//    @JsonProperty("BuyPrice") val buyPrice: Int
//)


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

