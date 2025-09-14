package io.oikkani.processorservice.domain.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot

interface AuctionPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshot>)
    fun findAllByItemCode(itemCode: Int): List<AuctionItemPriceSnapshot>
    fun deleteAll()
}