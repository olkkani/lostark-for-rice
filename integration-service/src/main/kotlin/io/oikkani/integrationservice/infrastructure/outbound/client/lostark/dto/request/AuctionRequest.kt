package io.oikkani.integrationservice.infrastructure.outbound.client.lostark.dto.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuctionRequest(
    val categoryCode: Int,
    val itemName: String,
    val pageNo: Int,
    val sort: String,
    val sortCondition: String,
)
