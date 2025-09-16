package io.oikkani.processorservice.application.port.inbound

import io.olkkani.common.dto.contract.MarketPriceSnapshot

interface MarketItemPriceSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcaPrice(marketPriceSnapshot: MarketPriceSnapshot)
    fun deleteAll()
}