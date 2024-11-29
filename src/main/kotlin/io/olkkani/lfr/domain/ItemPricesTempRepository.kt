package io.olkkani.lfr.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ItemPricesTempRepository: JpaRepository<ItemPricesTemp, Long> {
    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): MutableList<ItemPricesTemp>

//    fun getGemPriceRecordByRecordDate(recordDate: Instant)
}