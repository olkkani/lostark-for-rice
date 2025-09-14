package io.oikkani.processorservice.domain.outbound

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot

interface MarketPriceSnapshotRepositoryPort {
    fun saveAllNotExists(itemPriceSnapshots: List<MarketItemPriceSnapshot>)
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshot>
    fun deleteAll()
}