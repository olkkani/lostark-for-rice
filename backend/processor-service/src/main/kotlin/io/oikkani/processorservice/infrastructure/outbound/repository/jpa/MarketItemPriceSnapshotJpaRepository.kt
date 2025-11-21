package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshotEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MarketItemPriceSnapshotJpaRepository: JpaRepository<MarketItemPriceSnapshotEntity, Long>{
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshotEntity>

}