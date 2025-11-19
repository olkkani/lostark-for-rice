package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.application.dto.MarketItemPriceSnapshotDTO

interface MarketItemPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<MarketItemPriceSnapshotDTO>)
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshotDTO>
    fun deleteAll()
}