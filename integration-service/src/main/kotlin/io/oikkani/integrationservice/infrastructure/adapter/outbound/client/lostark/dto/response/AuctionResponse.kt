package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.oikkani.integrationservice.domain.dto.AuctionListingPrice
import java.time.LocalDateTime

//todo
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
) {
    fun toDomain() = items.map {
        AuctionListingPrice(
            price = it.auctionInfo.buyPrice,
            endDate = it.auctionInfo.endDate,
        )
    }
}

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionItem(
    val auctionInfo: AuctionInfo,
)

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionInfo(
    val buyPrice: Int,
    val endDate: LocalDateTime,
)

