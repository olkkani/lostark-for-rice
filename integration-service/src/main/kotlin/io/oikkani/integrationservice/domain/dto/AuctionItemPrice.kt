package io.oikkani.integrationservice.domain.dto

import java.time.LocalDateTime

class AuctionListingPrice(
    val price: Int,
    val endDate: LocalDateTime,
)