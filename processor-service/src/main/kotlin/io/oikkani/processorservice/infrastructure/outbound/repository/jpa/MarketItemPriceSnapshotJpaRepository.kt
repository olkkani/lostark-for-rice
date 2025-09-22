package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.MarketItemPriceSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MarketItemPriceSnapshotJpaRepository: JpaRepository<MarketItemPriceSnapshot, Long>{
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshot>

}