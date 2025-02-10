package io.olkkani.lfr.entity.jpa

import io.hypersistence.utils.hibernate.id.Tsid
import io.olkkani.lfr.dto.CandleChartResponse
import io.olkkani.lfr.dto.ItemPreview
import io.olkkani.lfr.entity.mongo.PriceRecord
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class ItemPriceIndex(
    @Id @Tsid
    val id: Long? = null,
    val itemCode: Int,
    val recordedDate: LocalDate,
    var closePrice: Int,
    var openPrice: Int,
    var highPrice: Int,
    var lowPrice: Int,
)

fun ItemPriceIndex.toPreviewResponse() = ItemPreview(
    price = closePrice,
    highPrice = highPrice,
    lowPrice = lowPrice,
    priceChange = closePrice - openPrice,
    priceChangeRate = ((closePrice * 1000) / (openPrice * 1000)).toDouble() / 1000000
)

fun ItemPriceIndex.toChartResponse() = CandleChartResponse(
    open = openPrice,
    high = highPrice,
    low = lowPrice,
    close = closePrice,
    time = recordedDate.toString()
)