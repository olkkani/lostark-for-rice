package io.oikkani.integrationservice.domain.dto

import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.request.AuctionRequest

class AuctionDTO(
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