package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot

interface AuctionItemPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshot>)
    fun findAllByItemCode(itemCode: Int): List<AuctionItemPriceSnapshot>
    fun deleteAll()
}