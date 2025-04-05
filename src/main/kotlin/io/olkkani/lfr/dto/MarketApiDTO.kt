package io.olkkani.lfr.dto

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

class MarketResponse (
    val items: Items
)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class Items (
    val id: Int,
    val currentMinPrice: Int,
    val yDayAvgPrice: Double,
)

fun MarketResponse.toMarketTodayPrice() = MarketTodayPrice(
    itemCode = items.id,
    price = items.currentMinPrice,
)

fun MarketResponse.toMarketPriceIndex() = MarketPriceIndex(
    itemCode = items.id,
    recordedDate = LocalDate.now(),
    openPrice = items.currentMinPrice,
    closePrice = items.currentMinPrice,
    highPrice = items.currentMinPrice,
    lowPrice = items.currentMinPrice,
)