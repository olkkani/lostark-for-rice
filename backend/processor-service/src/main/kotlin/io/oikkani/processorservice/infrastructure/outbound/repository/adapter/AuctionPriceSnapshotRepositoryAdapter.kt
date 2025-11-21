package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.AuctionItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.application.dto.AuctionItemPriceSnapshotDTO
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.AuctionItemPriceSnapshotJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.AuctionItemPriceSnapshotJpaRepository
import org.springframework.stereotype.Component

@Component
class AuctionPriceSnapshotRepositoryAdapter(
    private val jpaRepo: AuctionItemPriceSnapshotJpaRepository,
    private val jooqRepo: AuctionItemPriceSnapshotJooqRepository,
): AuctionItemPriceSnapshotRepositoryPort {

    override fun saveAllNotExists(itemPriceSnapshots: List<AuctionItemPriceSnapshotDTO>) {
        jooqRepo.insertIgnoreDuplicates(itemPriceSnapshots)
    }

    override fun findAllByItemCode(itemCode: Int): List<AuctionItemPriceSnapshotDTO> {
        return jpaRepo.findAllByItemCode(itemCode).map { it.toDomain() }
    }

    override fun deleteAll() {
        jooqRepo.truncateTable()
    }
}