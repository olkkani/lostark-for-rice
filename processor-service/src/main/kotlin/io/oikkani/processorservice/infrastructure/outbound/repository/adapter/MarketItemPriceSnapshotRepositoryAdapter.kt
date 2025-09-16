package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot
import org.springframework.stereotype.Component

@Component
class MarketItemPriceSnapshotRepositoryAdapter(

): MarketItemPriceSnapshotRepositoryPort{
    override fun saveAllNotExists(itemPriceSnapshots: List<MarketItemPriceSnapshot>) {
        TODO("Not yet implemented")
    }

    override fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshot> {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

}