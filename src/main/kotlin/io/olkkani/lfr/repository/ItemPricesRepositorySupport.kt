package io.olkkani.lfr.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.olkkani.lfr.entity.ItemPrices
import io.olkkani.lfr.entity.QItemPrices
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ItemPricesRepositorySupport(
    private val queryFactory: JPAQueryFactory,
) : QuerydslRepositorySupport(
    ItemPrices::class.java
) {

//    fun savePricesByDateTime(List<ItemPrices>){
//        queryFactory.insert()
//    }

    fun findPrevEightDaysPricesByItemCode(itemCode: Int): List<ItemPrices> {
        val yesterday = LocalDate.now().minusDays(1)
        val fiveDaysAgo = LocalDate.now().minusDays(8)
        return queryFactory.selectFrom(QItemPrices.itemPrices)
            .where(
                QItemPrices.itemPrices.itemCode.eq(itemCode)
                    .and(QItemPrices.itemPrices.recordedDate.between(fiveDaysAgo, yesterday))
            )
            .fetch()
    }

    fun findOldAllByItemCode(itemCode: Int): MutableList<ItemPrices> {
        val today = LocalDate.now()
        return queryFactory.selectFrom(QItemPrices.itemPrices).where(
            QItemPrices.itemPrices.itemCode.eq(itemCode)
                .and(
                    QItemPrices.itemPrices.recordedDate.ne(today)
                )
        ).orderBy(QItemPrices.itemPrices.recordedDate.asc()).fetch()
    }


}