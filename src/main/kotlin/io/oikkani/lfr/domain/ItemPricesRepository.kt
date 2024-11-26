package io.oikkani.lfr.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemPricesRepository: JpaRepository<ItemPrices, Long> {
//    fun getOldItemPrices
//    fun getGemPriceRecordByRecordDate(recordDate: Instant)
}