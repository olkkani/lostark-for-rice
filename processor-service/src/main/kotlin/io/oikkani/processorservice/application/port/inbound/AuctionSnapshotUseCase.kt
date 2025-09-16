package io.oikkani.processorservice.application.port.inbound

import io.olkkani.common.dto.contract.AuctionPriceSnapshot

interface AuctionSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcPrice(auctionPriceSnapshot: AuctionPriceSnapshot)
    fun deleteAll()
}