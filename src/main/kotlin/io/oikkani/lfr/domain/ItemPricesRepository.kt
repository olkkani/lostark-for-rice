package io.oikkani.lfr.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemPricesRepository: ReactiveCrudRepository<ItemPrices, Long> {
//    fun getGemPriceRecordByRecordDate(recordDate: Instant)
}