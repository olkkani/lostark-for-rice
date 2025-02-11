package io.olkkani.lfr.repository.jpa

import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ItemPriceIndexRepository: JpaRepository<ItemPriceIndex, Long> {
    fun findAllByItemCodeOrderByRecordedDateAsc(itemCode: Int): MutableList<ItemPriceIndex>

    @Query(
        """
        SELECT ip FROM ItemPriceIndex ip
        WHERE ip.itemCode = :itemCode 
        AND ip.recordedDate BETWEEN :startDate AND :endDate
        """
    )
    fun findPrevSixDaysPricesByItemCode(
        @Param("itemCode") itemCode: Int,
        @Param("startDate") startDate: LocalDate = LocalDate.now().minusDays(6),
        @Param("endDate") endDate: LocalDate = LocalDate.now().minusDays(1)
    ): List<ItemPriceIndex>

    fun findByItemCodeAndRecordedDate(itemCode: Int, recordedDate: LocalDate): ItemPriceIndex?
    fun findAllByRecordedDate(recordedDate: LocalDate): List<ItemPriceIndex>
}