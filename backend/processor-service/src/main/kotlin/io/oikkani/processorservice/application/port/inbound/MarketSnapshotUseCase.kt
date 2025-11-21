package io.oikkani.processorservice.application.port.inbound

import io.olkkani.common.dto.contract.MarketPriceSnapshotRequest

interface MarketSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcaPrice(snapshotRequest: MarketPriceSnapshotRequest)
    fun deleteAll()
}