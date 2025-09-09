package io.oikkani.processorservice.domain.out

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot

interface ItemPriceSnapshotAuctionRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshot>)
    fun findAll(): List<AuctionItemPriceSnapshot>
    fun deleteAll()
}