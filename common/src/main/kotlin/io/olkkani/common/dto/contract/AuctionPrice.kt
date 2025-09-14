package io.olkkani.common.dto.contract

import java.time.LocalDateTime

class AuctionPrice(
    val price: Int,
    val endDate: LocalDateTime,
)