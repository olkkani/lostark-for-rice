package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.ItemTodayPrices
import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.dto.toDomain
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import io.olkkani.lfr.util.LostarkAPIClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class AuctionGemService(
    private val repository: ItemPriceIndexRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) {
    private val logger = KotlinLogging.logger {}
    private val apiClient = LostarkAPIClient(apiKey)

    @Volatile
    var gems = mutableListOf<ItemTodayPrices>()



    fun clearTodayPricesRecord() {
        logger.info { "Clear Gems instance ${LocalDateTime.now().toString()}" }
        gems.clear()
        val gemList = collectGemInfoList
        gemList.forEach { gemInfo ->
            gems.add(ItemTodayPrices(itemCode = gemInfo.itemCode))
        }
    }



    fun updatePrevPriceTrend() {
        val gemList = collectGemInfoList
        val gemsPrices: MutableMap<Int, List<ItemPriceIndex>> = mutableMapOf()
        gemList.forEach { gemInfo ->
            gemsPrices[gemInfo.itemCode] = repository.findPrevSixDaysPricesByItemCode(gemInfo.itemCode)
        }

        gemList.forEach { gem ->
            for (i in 7 downTo 1) {
                val day = LocalDate.now().minusDays(i.toLong())
                val itemPrice = gemsPrices[gem.itemCode]!!.find { it.recordedDate == day }?.closePrice ?: 0
                val prevItemPrice =
                    gemsPrices[gem.itemCode]!!.find { it.recordedDate == day.minusDays(1) }?.closePrice ?: 0
                val pairItemPrice = gemsPrices[gem.pairItemCode]!!.find { it.recordedDate == day }?.closePrice ?: 0

                if (itemPrice != 0 && pairItemPrice != 0) {
                    gems[itemPrice].updatePairItemPriceGapAndRate(
                        date = day,
                        priceGap = itemPrice - pairItemPrice,
                        priceGapRate = itemPrice.toDouble() / pairItemPrice.toDouble()
                    )
                }
                if (itemPrice != 0 && prevItemPrice != 0) {
                    gems[itemPrice].updatePrevItemPriceGapAndRate(
                        date = day,
                        priceGap = itemPrice - pairItemPrice,
                        priceGapRate = itemPrice.toDouble() / pairItemPrice.toDouble()
                    )
                }
            }
        }

    }

    suspend fun updatePairItemPriceGap() = coroutineScope {
        val gemList = collectGemInfoList
        val today = LocalDate.now()

        gemList.map { gemInfo ->
            async {
                val gemPrice = gems[gemInfo.itemCode].close
                val pairGemPrice = gems[gemInfo.pairItemCode].close

                gems[gemInfo.itemCode].updatePairItemPriceGapAndRate(
                    date = today,
                    priceGap = gemPrice - pairGemPrice,
                    priceGapRate = gemPrice.toDouble() / pairGemPrice.toDouble()
                )
            }
        }.awaitAll()
    }

    fun saveTodayPrices() {
        gems.forEach { gem ->
            if (gem.todayPrices.isNotEmpty()) {
                repository.save(gem.toDomain())
            }
        }
    }




    fun getPricesByItemCode(itemCode: Int): List<ItemPriceIndex> {
        val prices = repository.findOldAllByItemCode(itemCode)
        // mutableList 에서 List 로 바꾼 후 발생하는 에러 추후 수정 시 참고
//        gems.find { it.itemCode == itemCode }?.let { prices.add(it.toDomain()) }
        return prices
    }

    fun getAllKindsTodayPrice(): MutableList<ItemTodayPrices> {
        return gems
    }


}