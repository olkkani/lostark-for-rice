package io.olkkani.lfr.dao

import io.olkkani.lfr.dto.AuctionRequest

data class Gem (
    val itemCode: Int,
    val pairItemCode: Int,
    val itemName: String,
)

fun Gem.toRequest() = AuctionRequest(
    itemName = itemName,
)

val gems = listOf(
    Gem(itemCode = 65021100, pairItemCode = 65022100, itemName = "10레벨 멸화의 보석"),
    Gem(itemCode = 65022100, pairItemCode = 65021100, itemName = "10레벨 홍염의 보석"),
    Gem(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석"),
    Gem(itemCode = 65032080, pairItemCode = 65031080, itemName = "8레벨 작열의 보석"),
    Gem(itemCode = 65031100, pairItemCode = 65032100, itemName = "10레벨 겁화의 보석"),
    Gem(itemCode = 65032100, pairItemCode = 65031100, itemName = "10레벨 작열의 보석")
)

