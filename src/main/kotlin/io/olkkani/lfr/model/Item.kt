package io.olkkani.lfr.model

import io.olkkani.lfr.domain.ItemPrices
import io.olkkani.lfr.util.IQRCalculator
import java.time.LocalDate
import java.time.LocalDateTime

data class Item(
    val itemCode: Int
) {
    var open: Int = 0
    var high: Int = 0
    var low: Int = 0
    var close: Int = 0
    val time: LocalDate = LocalDate.now()
    var todayPrices = mutableMapOf<LocalDateTime, Int>()


    fun addTodayPrices(prices: Map<LocalDateTime, Int>) {
        val iqrCalculator = IQRCalculator(prices.map { it.value.toDouble() })

        close = iqrCalculator.getMin().toInt()
        if (high < iqrCalculator.getMax().toInt()) {
            high = iqrCalculator.getMax().toInt()
        }
        if (low > close || low == 0) {
            low = close
        }
        if (open == 0) {
            open = close
        }

        for ((time, price) in prices) {
            todayPrices.getOrPut(time) { price }
        }
    }
}

fun Item.toDomain() = ItemPrices(
    itemCode = itemCode,
    closePrice = close,
    openPrice = open,
    highPrice = high,
    lowPrice = low,
    recordedDate = time
)