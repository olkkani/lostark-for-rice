package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.application.dto.AuctionItemPriceSnapshotDTO

interface AuctionItemPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshotDTO>)
    fun findAllByItemCode(itemCode: Int): List<AuctionItemPriceSnapshotDTO>
    fun deleteAll()
}