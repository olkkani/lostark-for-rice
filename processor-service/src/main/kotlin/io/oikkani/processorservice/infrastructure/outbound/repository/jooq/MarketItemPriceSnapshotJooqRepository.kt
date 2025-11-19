package io.oikkani.processorservice.infrastructure.outbound.repository.jooq

import com.github.f4b6a3.tsid.TsidCreator
import io.oikkani.processorservice.application.dto.MarketItemPriceSnapshotDTO
import org.jooq.DSLContext
import org.jooq.generated.Tables
import org.springframework.stereotype.Repository

@Repository
class MarketItemPriceSnapshotJooqRepository(private val dsl: DSLContext) {
    private val table = Tables.MARKET_ITEM_PRICE_SNAPSHOTS

    fun insertIgnoreDuplicates(itemPriceSnapshots: List<MarketItemPriceSnapshotDTO>) {
        if (itemPriceSnapshots.isEmpty()) return

        val query = dsl.insertInto(
            table,
            table.ID,
            table.ITEM_CODE,
            table.PRICE
        )
        itemPriceSnapshots.forEach { snapshot ->
            query.values(
                snapshot.id ?: TsidCreator.getTsid().toLong(),
                snapshot.itemCode,
                snapshot.price,
            )
        }
        query.onDuplicateKeyIgnore().execute()
    }

    fun truncateTable() {
        dsl.truncate(table).execute()
    }
}