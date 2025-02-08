package io.olkkani.lfr.entity.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate


@Document(collection = "itemPriceIndexTrend")
class ItemPriceIndexTrend(
    @Id
    val id: ObjectId? = null,
    val itemCode: Int,
    var priceRecords: List<PriceRecord>
)
class PriceRecord (
    val date: LocalDate,
    var prevGepPrice: Int,
    var prevGapPriceRate: Double,
    var pairGapPrice: Int,
    var pairGapPriceRate: Double,
)

fun ItemPriceIndexTrend.toResponse(): List<PriceRecord> {
    return priceRecords
}