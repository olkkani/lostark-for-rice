package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.domain.out.ItemPriceSnapshotAuctionRepositoryPort
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.ItemPriceSnapshotAuctionJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.ItemPriceSnapshotAuctionJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ItemPriceSnapshotAuctionRepositoryAdapter(
    private val jpaRepo: ItemPriceSnapshotAuctionJpaRepository,
    private val jooqRepo: ItemPriceSnapshotAuctionJooqRepository,
): ItemPriceSnapshotAuctionRepositoryPort {

    override fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshot>) {
        jooqRepo.insertIgnoreDuplicates(itemPriceSnapshots)
    }

    override fun findAll(): List<AuctionItemPriceSnapshot> {
        return jpaRepo.findAll()
    }

    override fun deleteAll() {
        jooqRepo.truncateTable()
    }
}