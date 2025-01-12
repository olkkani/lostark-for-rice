package io.olkkani.lfr.repository

import io.olkkani.lfr.entity.ItemPrices
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemPricesRepository: JpaRepository<ItemPrices, Long> {
    fun findAllByItemCode(itemCode: Int): MutableList<ItemPrices>


//    fun getOldItemPrices
//    fun getGemPriceRecordByRecordDate(recordDate: Instant)
}