package io.oikkani.processorservice.infrastructure.outbound.repository.jooq

import com.github.f4b6a3.tsid.TsidCreator
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.AuctionItemPriceSnapshot
import org.jooq.DSLContext
import org.jooq.generated.Tables
import org.springframework.stereotype.Repository

@Repository
class AuctionItemPriceSnapshotJooqRepository(private val dsl: DSLContext) {

    private val table = Tables.AUCTION_ITEM_PRICE_SNAPSHOTS

    fun insertIgnoreDuplicates(itemPriceSnapshots: List<AuctionItemPriceSnapshot>) {
        if (itemPriceSnapshots.isEmpty()) return
        val itemPriceSnapshot = Tables.AUCTION_ITEM_PRICE_SNAPSHOTS
        val query = dsl.insertInto(
            itemPriceSnapshot,
            itemPriceSnapshot.ID,
            itemPriceSnapshot.ITEM_CODE,
            itemPriceSnapshot.END_DATE,
            itemPriceSnapshot.PRICE
        )
        itemPriceSnapshots.forEach { snapshot ->
            query.values(
                snapshot.id ?: TsidCreator.getTsid().toLong(),
                snapshot.itemCode,
                snapshot.endDate,
                snapshot.price
            )
        }
        query.onDuplicateKeyIgnore().execute()
    }



    fun truncateTable() {
        dsl.truncateTable(table).execute()
    }
}