//package io.oikkani.processorservice.infrastructure.out.repository
//
//import io.oikkani.processorservice.domain.entity.ItemPreviousPriceChange
//import io.olkkani.lfr.repository.entity.ItemPreviousPriceChange
//import jakarta.persistence.EntityManager
//import org.jooq.DSLContext
//import org.jooq.generated.Tables
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.stereotype.Repository
//import java.time.LocalDate
//
//
//@Repository
//interface ItemPreviousPriceChangeRepo: JpaRepository<ItemPreviousPriceChange, Long>, ItemPreviousPriceChangeRepoSupport {
//    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): ItemPreviousPriceChange?
//    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<ItemPreviousPriceChange>
//    fun deleteAllByRecordedDate(recordedDate: LocalDate)
//}
//
//fun interface ItemPreviousPriceChangeRepoSupport {
//    fun truncateTable()
//}
//
//class ItemPreviousPriceChangeRepoSupportImpl(private val dsl: DSLContext, private val entityManager: EntityManager): ItemPreviousPriceChangeRepoSupport{
//   override fun truncateTable(){
//       dsl.truncate(Tables.ITEM_PREVIOUS_PRICE_CHANGES).execute()
//       entityManager.clear()
//    }
//}