package io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto

import io.oikkani.integrationservice.domain.dto.AuctionListingPrice

class AuctionPriceRequest(
    val itemCode: Int,
    val prices: List<AuctionListingPrice>,
    val isOpeningJob: Boolean = false,
)