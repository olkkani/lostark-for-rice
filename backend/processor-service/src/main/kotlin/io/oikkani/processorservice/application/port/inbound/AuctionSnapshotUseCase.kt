package io.oikkani.processorservice.application.port.inbound

import io.olkkani.common.dto.contract.AuctionItemPrice

interface AuctionSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcPrice(auctionItemPrice: AuctionItemPrice)
    fun deleteAll()
}