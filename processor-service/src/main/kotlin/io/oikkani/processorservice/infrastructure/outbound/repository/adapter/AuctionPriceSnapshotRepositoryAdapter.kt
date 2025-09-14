package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.domain.outbound.AuctionPriceSnapshotRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.AuctionItemPriceSnapshotJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemPriceSnapshotJpaRepository
import org.springframework.stereotype.Repository

@Repository
class AuctionPriceSnapshotRepositoryAdapter(
    private val jpaRepo: AuctionItemPriceSnapshotJpaRepository,
    private val jooqRepo: AuctionItemPriceSnapshotJooqRepository,
): AuctionPriceSnapshotRepositoryPort {

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