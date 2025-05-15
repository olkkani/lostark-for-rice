package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.olkkani.lfr.entity.mongo.MarketTodayPrice

class MarketRequest(
    val categoryCode: Int,
    val itemName: String,
    var pageNo: Int = 1,
)

data class MarketResponse (
   @JsonProperty("Items") val items: List<Items>
)
data class Items (
    @JsonProperty("Id") val id: Int,
    @JsonProperty("CurrentMinPrice") val currentMinPrice: Int,
    @JsonProperty("YDayAvgPrice") val yDayAvgPrice: Float,
){
   fun toMarketTodayPrice() = MarketTodayPrice(
       itemCode = id,
       price = currentMinPrice,
   )
}