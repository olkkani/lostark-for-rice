package io.oikkani.processorservice.application.port.inbound

interface MarketItemPriceSnapshotUseCase {
    fun saveSnapshotAndUpdateHlcaPrice(marketItemPriceSnapshot: String)
    fun deleteAll()
}