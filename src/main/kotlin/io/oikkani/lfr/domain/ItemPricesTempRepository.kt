package io.oikkani.lfr.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDate

@Repository
interface ItemPricesTempRepository: ReactiveCrudRepository<ItemPricesTemp, Long> {
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): Flux<ItemPricesTemp>

//    fun getGemPriceRecordByRecordDate(recordDate: Instant)
}