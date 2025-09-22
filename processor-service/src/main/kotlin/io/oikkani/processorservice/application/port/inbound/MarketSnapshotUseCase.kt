package io.oikkani.processorservice.application.port.inbound

import io.olkkani.common.dto.contract.MarketPriceSnapshot

interface MarketSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcaPrice(snapshot: MarketPriceSnapshot)
    fun deleteAll()
}