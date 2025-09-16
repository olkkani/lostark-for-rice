package io.olkkani.common.dto.contract

import java.time.LocalDateTime

class MarketPrice (
    val itemCode: Int,
    val price: Int,
    val yDateAvgPrice: Int = 0,
    val endDate: LocalDateTime,
)