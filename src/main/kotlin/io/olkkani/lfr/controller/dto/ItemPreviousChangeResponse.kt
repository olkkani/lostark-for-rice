package io.olkkani.lfr.controller.dto

import java.time.LocalDate

class ItemPreviousChangeResponse(
    val recordedDate: LocalDate,
    val price: Int,

    val priceDiffPrevDay: Int,
    val priceDiffRatePrevDay: Double,

    val priceDiffPairItem: Int? = null,
    val priceDiffRatePairItem: Double? = null,
)