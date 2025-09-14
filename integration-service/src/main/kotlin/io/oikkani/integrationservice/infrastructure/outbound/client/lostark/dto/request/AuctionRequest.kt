package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.request

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
