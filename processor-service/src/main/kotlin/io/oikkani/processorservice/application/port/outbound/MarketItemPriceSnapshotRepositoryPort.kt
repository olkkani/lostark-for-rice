package io.oikkani.processorservice.application.port.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot

interface MarketItemPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<MarketItemPriceSnapshot>)
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshot>
    fun deleteAll()
}