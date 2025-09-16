package io.oikkani.processorservice.application.service

import io.oikkani.processorservice.application.port.inbound.MarketItemPriceSnapshotUseCase
import io.olkkani.common.dto.contract.MarketPriceSnapshot
import org.springframework.stereotype.Service

@Service
class MarketItemPriceSnapshotService(

) : MarketItemPriceSnapshotUseCase {

    override fun saveSnapshotAndUpdateHlcaPrice(marketPriceSnapshot: MarketPriceSnapshot) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }
}