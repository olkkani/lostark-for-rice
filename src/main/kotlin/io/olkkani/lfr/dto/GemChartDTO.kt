package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.olkkani.lfr.domain.ItemPrices

data class ResponseData(
    @JsonProperty("Items") val items: List<Item>
)

data class Item(
    @JsonProperty("AuctionInfo") val auctionInfo: AuctionInfo
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

