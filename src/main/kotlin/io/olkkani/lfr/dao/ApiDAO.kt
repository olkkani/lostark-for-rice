package io.olkkani.lfr.dao

import io.olkkani.lfr.dto.AuctionRequest
import io.olkkani.lfr.dto.MarketRequest

class FusionMaterialDAO(
    val categoryCode: Int,
    val name: String,
)

fun FusionMaterialDAO.toRequest() = MarketRequest(
    categoryCode = categoryCode,
    itemName = name,
)


class GemDAO (
    val itemCode: Int,
    val pairItemCode: Int,
    val name: String,
)

fun GemDAO.toRequest() = AuctionRequest(
    itemName = name,
)