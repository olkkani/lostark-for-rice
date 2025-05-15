package io.olkkani.lfr.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.lfr.entity.mongo.AuctionTodayPrice
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionRequest(
    val itemName: String,
    val categoryCode: Int = 210000,
    val pageNo: Int = 0,
    val sort: String = "BUY_PRICE",
    val sortCondition: String = "ASC",
)

data class AuctionResponse(
    @JsonProperty("Items")val items: List<AuctionItem>
){

}
data class AuctionItem(
    @JsonProperty("AuctionInfo")val auctionInfo: AuctionInfo,
)
data class AuctionInfo(
    @JsonProperty("BuyPrice")val buyPrice: Int,
    @JsonProperty("EndDate")val endDate: LocalDateTime
)

fun AuctionResponse.extractPrices(): List<Int> {
    return items.map { it.auctionInfo.buyPrice }
}

fun AuctionResponse.toTodayItemPrices(itemCode: Int): List<AuctionTodayPrice> {
    return items.map {
        AuctionTodayPrice(
            itemCode = itemCode,
            endDate = it.auctionInfo.endDate,
            price = it.auctionInfo.buyPrice,
        )
    }
}





