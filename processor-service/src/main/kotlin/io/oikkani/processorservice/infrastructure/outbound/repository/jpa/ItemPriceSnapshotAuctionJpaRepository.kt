package io.oikkani.processorservice.infrastructure.outbound.repository.jpa

import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot
import org.springframework.data.jpa.repository.JpaRepository

interface ItemPriceSnapshotAuctionJpaRepository: JpaRepository<AuctionItemPriceSnapshot, Long>{

}