package io.olkkani.lfr.dto

import io.olkkani.lfr.entity.ItemPrices
import io.olkkani.lfr.util.IQRCalculator
import java.time.LocalDate
import java.time.LocalDateTime

data class ItemTodayPrices(
    val itemCode: Int
) {
    var open: Int = 0
    var high: Int = 0
    var low: Int = 0
    var close: Int = 0
    val time: LocalDate = LocalDate.now()
    var todayPrices = mutableMapOf<LocalDateTime, Int>()
    val priceFiveDaysTrend = mutableMapOf<LocalDate, PriceTrend>()

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
        updateYesterdayPriceGap()
    }

    fun updatePairItemPriceGapAndRate(date: LocalDate, priceGap: Int, priceGapRate: Double) {
        if (priceFiveDaysTrend[date] != null) {
            priceFiveDaysTrend[date]?.apply {
                pairItemPriceGap = priceGap
                pairItemPriceGapRate = priceGapRate
            }
        } else {
            priceFiveDaysTrend[date] = PriceTrend().apply {
                pairItemPriceGap = priceGap
                pairItemPriceGapRate = priceGapRate
            }
        }
    }

    fun updatePrevItemPriceGapAndRate(date: LocalDate, priceGap: Int, priceGapRate: Double) {
        if (priceFiveDaysTrend[date] != null) {
            priceFiveDaysTrend[date]?.apply {
                prevPriceGap = priceGap
                prevPriceGapRate = priceGapRate
            }
        } else {
            priceFiveDaysTrend[date] = PriceTrend().apply {
                prevPriceGap = priceGap
                prevPriceGapRate = priceGapRate
            }
        }
    }

    private fun updateYesterdayPriceGap() {
        val yesterdayPriceTrend = priceFiveDaysTrend[LocalDate.now().minusDays(1)] ?: return

        if (priceFiveDaysTrend[LocalDate.now()] != null) {
            priceFiveDaysTrend[LocalDate.now()]?.apply {
                price = close
                prevPriceGap = close - yesterdayPriceTrend.price
                prevPriceGapRate = close.toDouble() / yesterdayPriceTrend.price.toDouble()
            }
        } else {
            priceFiveDaysTrend[LocalDate.now()] = PriceTrend().apply {
                price = close
                prevPriceGap = close - yesterdayPriceTrend.price
                prevPriceGapRate = close.toDouble() / yesterdayPriceTrend.price.toDouble()
            }
        }
    }
}

class PriceTrend {
    var price: Int = 0
    var prevPriceGap: Int = 0
    var prevPriceGapRate: Double = 0.0
    var pairItemPriceGap: Int = 0
    var pairItemPriceGapRate: Double = 0.0
}

fun ItemTodayPrices.toDomain() = ItemPrices(
    itemCode = itemCode,
    closePrice = close,
    openPrice = open,
    highPrice = high,
    lowPrice = low,
    recordedDate = time
)