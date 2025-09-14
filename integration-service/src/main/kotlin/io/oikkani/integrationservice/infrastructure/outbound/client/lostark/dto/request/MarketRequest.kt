package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.request

data class MarketRequest(
    val categoryCode: Int,
    val itemName: String? = null,
    val itemGrade: String? = null,
    var pageNo: Int = 1,
)