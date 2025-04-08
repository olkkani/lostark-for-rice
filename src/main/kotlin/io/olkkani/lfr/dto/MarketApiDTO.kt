package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.lfr.entity.jpa.MarketPriceIndex
import io.olkkani.lfr.entity.mongo.MarketTodayPrice
import java.time.LocalDate

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
    @JsonProperty("YDayAvgPrice") val yDayAvgPrice: Double,
)

fun Items.toMarketTodayPrice() = MarketTodayPrice(
    itemCode = id,
    price = currentMinPrice,
)