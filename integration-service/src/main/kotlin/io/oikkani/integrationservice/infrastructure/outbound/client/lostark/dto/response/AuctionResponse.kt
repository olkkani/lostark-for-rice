package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.olkkani.common.dto.contract.AuctionPrice
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionResponse(
    val items: List<AuctionItem>,
) {
    fun toDomain() = items.map {
        AuctionPrice(
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

