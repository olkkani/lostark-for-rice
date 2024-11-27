package io.olkkani.lfr.domain

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ItemPricesRepositorySupport (
    private val queryFactory: JPAQueryFactory,
): QuerydslRepositorySupport(ItemPrices::class.java
){
    fun findOldAllByItemCode(itemCode: Int): List<ItemPrices> {
        val today = LocalDate.now()
        return queryFactory.selectFrom(QItemPrices.itemPrices).where(
            QItemPrices.itemPrices.itemCode.eq(itemCode)
                .and(
                    QItemPrices.itemPrices.recordedDate.ne(today)
                )
        ).fetch()
    }
}