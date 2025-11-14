package io.oikkani.processorservice.infrastructure.outbound.repository.adapter

import io.oikkani.processorservice.application.port.outbound.MarketItemPriceSnapshotRepositoryPort
import io.oikkani.processorservice.domain.model.MarketItemPriceSnapshotDTO
import io.oikkani.processorservice.infrastructure.outbound.repository.jooq.MarketItemPriceSnapshotJooqRepository
import io.oikkani.processorservice.infrastructure.outbound.repository.jpa.MarketItemPriceSnapshotJpaRepository
import org.springframework.stereotype.Component

@Component
class MarketItemPriceSnapshotRepositoryAdapter(
    private val jpaRepo: MarketItemPriceSnapshotJpaRepository,
    private val jooqRepository: MarketItemPriceSnapshotJooqRepository,

): MarketItemPriceSnapshotRepositoryPort{
    override fun saveAllNotExists(itemPriceSnapshots: List<MarketItemPriceSnapshotDTO>) {
        jooqRepository.insertIgnoreDuplicates(itemPriceSnapshots)
    }

    override fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshotDTO> {
        return jpaRepo.findAllByItemCode(itemCode).map { it.toDomain() }
    }

    override fun deleteAll() {
        jooqRepository.truncateTable()
    }

}