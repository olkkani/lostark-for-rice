package io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming




@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
data class AuctionRequest(
    val categoryCode: Int,
    val itemName: String,
    val pageNo: Int,
    val sort: String,
    val sortCondition: String,
)


class AuctionDTO (
    val categoryCode: Int = 210000,
    val sort: String = "BUY_PRICE",
    val sortCondition: String = "ASC",
    val itemName: String,
    val pageNo: Int = 0,
    val itemCode: Int,
    val pairItemCode: Int,
){
    fun toGemRequest() = AuctionRequest(
        categoryCode = categoryCode,
        itemName = itemName,
        pageNo = pageNo,
        sort = sort,
        sortCondition = sortCondition,
    )
}