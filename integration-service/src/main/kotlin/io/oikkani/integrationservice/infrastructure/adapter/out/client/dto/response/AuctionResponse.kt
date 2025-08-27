package io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

//class AuctionResponse (
//    val categoryCode: Int = 210000,
//    val sort: String = "BUY_PRICE",
//    val sortCondition: String = "ASC",
//    val itemName: String,
//    val pageNo: Int = 0,
//    val itemCode: Int,
//    val pairItemCode: Int,
//)
//




@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionResponse(
    val items: List<AuctionItem>,
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionItem(
    val auctionInfo: AuctionInfo,
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionInfo(
    val buyPrice: Int,
    val endDate: LocalDateTime,
)

