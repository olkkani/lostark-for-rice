package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.AuctionItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.AuctionItemPriceSnapshotJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemPriceSnapshotJpaRepository
import org.springframework.stereotype.Component

@Component
class AuctionPriceSnapshotRepositoryAdapter(
    private val jpaRepo: AuctionItemPriceSnapshotJpaRepository,
    private val jooqRepo: AuctionItemPriceSnapshotJooqRepository,
): AuctionItemPriceSnapshotRepositoryPort {

    override fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshot>) {
        jooqRepo.insertIgnoreDuplicates(itemPriceSnapshots)
    }

    override fun findAllByItemCode(itemCode: Int): List<AuctionItemPriceSnapshot> {
        return jpaRepo.findAllByItemCode(itemCode)
    }

    override fun deleteAll() {
        jooqRepo.truncateTable()
    }
}