package io.olkkani.lfr.entity.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate


@Document(collection = "recentPriceIndexTrend")
class RecentPriceIndexTrend(
    @Id
    val id: ObjectId? = null,
    val itemCode: Int,
    var priceRecords: MutableList<TodayPriceGap> = mutableListOf<TodayPriceGap>(),
)
class TodayPriceGap (
    val date: LocalDate,
    var price: Int,
    var prevGapPrice: Int,
    var prevGapPriceRate: Double,
    var pairGapPrice: Int,
    var pairGapPriceRate: Double,
)

fun RecentPriceIndexTrend.toResponse(): List<TodayPriceGap> {
    return priceRecords.sortedByDescending { it.date }
}