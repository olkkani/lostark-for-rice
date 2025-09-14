package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.MarketItemPriceSnapshotUseCase
import org.springframework.stereotype.Service

@Service
class MarketItemPriceSnapshotService(

) : MarketItemPriceSnapshotUseCase {
    override fun saveSnapshotAndUpdateHlcaPrice(marketItemPriceSnapshot: String) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }
}