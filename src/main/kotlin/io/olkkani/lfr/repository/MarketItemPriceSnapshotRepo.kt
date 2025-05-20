package io.olkkani.lfr.repository

import com.github.f4b6a3.tsid.TsidCreator
import io.olkkani.lfr.dao.PriceRange
import io.olkkani.lfr.entity.MarketItemPriceSnapshot
import jakarta.persistence.EntityManager
import org.jooq.DSLContext
import org.jooq.generated.Tables
import org.jooq.impl.DSL.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface MarketItemPriceSnapshotRepo : JpaRepository<MarketItemPriceSnapshot, Long>,
    MarketItemPriceSnapshotRepoSupport {
    fun findAllByItemCode(itemCode: Int): List<MarketItemPriceSnapshot>
}

interface MarketItemPriceSnapshotRepoSupport {
    fun saveIgnoreDuplicates(itemPriceSnapshot: MarketItemPriceSnapshot)
    fun saveAllIgnoreDuplicates(itemPriceSnapshots: List<MarketItemPriceSnapshot>)
    fun findFilteredPriceRangeByItemCode(itemCode: Int): PriceRange
    fun truncateTable()
}

@Repository
class MarketItemPriceSnapshotRepoSupportImpl(private val dsl: DSLContext, private val entityManager: EntityManager) :
    MarketItemPriceSnapshotRepoSupport {
    override fun saveIgnoreDuplicates(itemPriceSnapshot: MarketItemPriceSnapshot) {
        val itemPriceSnapshotTable = Tables.MARKET_ITEM_PRICE_SNAPSHOTS
        dsl.insertInto(
            itemPriceSnapshotTable,
            itemPriceSnapshotTable.ID,
            itemPriceSnapshotTable.ITEM_CODE,
            itemPriceSnapshotTable.PRICE
        )
            .values(
                itemPriceSnapshot.id ?: TsidCreator.getTsid().toLong(),
                itemPriceSnapshot.itemCode,
                itemPriceSnapshot.price
            )
            .onDuplicateKeyIgnore()
            .execute()

    }

    override fun saveAllIgnoreDuplicates(itemPriceSnapshots: List<MarketItemPriceSnapshot>) {
        if (itemPriceSnapshots.isEmpty()) return
        val itemPriceSnapshotTable = Tables.MARKET_ITEM_PRICE_SNAPSHOTS
        val query = dsl.insertInto(
            itemPriceSnapshotTable,
            itemPriceSnapshotTable.ID,
            itemPriceSnapshotTable.ITEM_CODE,
            itemPriceSnapshotTable.PRICE
        )

        itemPriceSnapshots.forEach { snapshot ->
            query.values(
                snapshot.id ?: TsidCreator.getTsid().toLong(),
                snapshot.itemCode,
                snapshot.price
            )
        }
        query.onDuplicateKeyIgnore().execute()
    }

    override fun findFilteredPriceRangeByItemCode(itemCode: Int): PriceRange {
        val marketSnapshotTable = Tables.MARKET_ITEM_PRICE_SNAPSHOTS
        // Q1과 Q3 계산
        val quartiles = dsl.select(
            percentileCont(0.25).withinGroupOrderBy(marketSnapshotTable.PRICE).`as`("q1"),
            percentileCont(0.75).withinGroupOrderBy(marketSnapshotTable.PRICE).`as`("q3")
        )
            .from(marketSnapshotTable)
            .where(marketSnapshotTable.ITEM_CODE.eq(itemCode)) // 동일한 itemCode에 대해 계산
            .fetchOne()

        val q1 = quartiles?.get("q1", Double::class.java) ?: 0.0
        val q3 = quartiles?.get("q3", Double::class.java) ?: 0.0
        val iqr = q3 - q1

        // IQR 기반 이상치 제거 범위 계산
        val lowerBound = q1 - 1.5 * iqr
        val upperBound = q3 + 1.5 * iqr

        // 이상치 제거 범위 내에서의 min/max 값 추출
        val minPriceField = field("min_price", Int::class.java)
        val maxPriceField = field("max_price", Int::class.java)
        return dsl.select(
            min(marketSnapshotTable.PRICE).`as`("min_price"),
            max(marketSnapshotTable.PRICE).`as`("max_price")
        )
            .from(marketSnapshotTable)
            .where(
                marketSnapshotTable.ITEM_CODE.eq(itemCode) // 동일한 itemCode 조건
                    .and(marketSnapshotTable.PRICE.between(lowerBound.toInt(), upperBound.toInt())) // 이상치 제거 조건
            )
            .fetchOne()?.let {
                PriceRange(
                    it[minPriceField] ?: 0,
                    it[maxPriceField] ?: 0
                )
            } ?: PriceRange(0, 0)
    }

    override fun truncateTable() {
        dsl.truncate(Tables.MARKET_ITEM_PRICE_SNAPSHOTS).execute()
        entityManager.clear()
    }
}

