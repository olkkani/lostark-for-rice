package io.olkkani.lfr.domain

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import io.oikkani.lfr.domain.QItemPrices

@Repository
class ItemPricesRepositorySupport (
    private val queryFactory: JPAQueryFactory,
): QuerydslRepositorySupport(ItemPrices::class.java
){
    fun findOldAllByItemCode(itemCode: Int) = queryFactory.select(QItemPrices.Companion).fetch()
}