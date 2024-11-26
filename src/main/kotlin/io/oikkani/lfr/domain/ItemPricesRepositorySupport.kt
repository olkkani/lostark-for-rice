package io.oikkani.lfr.domain

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ItemPricesRepositorySupport (
    private val queryFactory: JPAQueryFactory,
): QuerydslRepositorySupport(ItemPrices::class.java
){

}