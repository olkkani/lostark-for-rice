//package io.oikkani.processorservice.infrastructure.out.repository
//
//import com.github.f4b6a3.tsid.TsidCreator
//import io.oikkani.processorservice.domain.entity.AuctionItemPriceSnapshot
//import io.oikkani.processorservice.infrastructure.out.repository.dto.PriceRange
//import io.olkkani.lfr.repository.dto.PriceRange
//import io.olkkani.lfr.repository.entity.AuctionItemPriceSnapshot
//import org.jooq.DSLContext
//import org.jooq.generated.Tables
//import org.jooq.impl.DSL.*
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.stereotype.Repository
//import kotlin.text.get
//
//interface AuctionItemPriceSnapshotRepo: JpaRepository<AuctionItemPriceSnapshot, Long>, AuctionItemPriceSnapshotRepoSupport{
//    fun findAllByItemCode(itemCode: Long): List<AuctionItemPriceSnapshot>
//}
//
//interface AuctionItemPriceSnapshotRepoSupport{
//    fun saveAllIgnoreDuplicates(itemPriceSnapshots: List<AuctionItemPriceSnapshot>)
//    fun findFilteredPriceRangeByItemCode(itemCode: Int): PriceRange
//}
//
//@Repository
//class AuctionItemPriceSnapshotRepoSupportImpl(private val dsl: DSLContext): AuctionItemPriceSnapshotRepoSupport {
//    override fun saveAllIgnoreDuplicates(itemPriceSnapshots: List<AuctionItemPriceSnapshot>) {

//    }
//    override fun findFilteredPriceRangeByItemCode(itemCode: Int): PriceRange {
//        val auctionSnapshotTable = Tables.AUCTION_ITEM_PRICE_SNAPSHOTS
//        // Q1과 Q3 계산
//        val quartiles = dsl.select(
//            percentileCont(0.25).withinGroupOrderBy(auctionSnapshotTable.PRICE).`as`("q1"),
//            percentileCont(0.75).withinGroupOrderBy(auctionSnapshotTable.PRICE).`as`("q3")
//        )
//            .from(auctionSnapshotTable)
//            .where(auctionSnapshotTable.ITEM_CODE.eq(itemCode)) // 동일한 itemCode에 대해 계산
//            .fetchOne()
//
//        val q1 = quartiles?.get("q1", Double::class.java) ?: 0.0
//        val q3 = quartiles?.get("q3", Double::class.java) ?: 0.0
//        val iqr = q3 - q1
//
//        // IQR 기반 이상치 제거 범위 계산
//        val lowerBound = q1 - 1.5 * iqr
//        val upperBound = q3 + 1.5 * iqr
//
//        // 이상치 제거 범위 내에서의 min/max 값 추출
//        val minPriceField = field("min_price", Int::class.java)
//        val maxPriceField = field("max_price", Int::class.java)
//        return dsl.select(
//            min(auctionSnapshotTable.PRICE).`as`("min_price"),
//            max(auctionSnapshotTable.PRICE).`as`("max_price")
//        )
//            .from(auctionSnapshotTable)
//            .where(
//                auctionSnapshotTable.ITEM_CODE.eq(itemCode) // 동일한 itemCode 조건
//                    .and(auctionSnapshotTable.PRICE.between(lowerBound.toInt(), upperBound.toInt())) // 이상치 제거 조건
//            )
//            .fetchOne()?.let {
//                PriceRange(
//                    it[minPriceField] ?: 0,
//                    it[maxPriceField] ?: 0
//                )
//            } ?: PriceRange(0, 0)
//    }
//
//    override fun truncateTable() {
//        dsl.truncate(Tables.AUCTION_ITEM_PRICE_SNAPSHOTS).execute()
//    }
//}