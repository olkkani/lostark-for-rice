package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.ItemTodayPrices
import io.olkkani.lfr.dto.PriceTrend
import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.dto.toDomain
import io.olkkani.lfr.entity.ItemPrices
import io.olkkani.lfr.entity.toTempDomains
import io.olkkani.lfr.repository.ItemPricesRepository
import io.olkkani.lfr.repository.ItemPricesRepositorySupport
import io.olkkani.lfr.repository.ItemPricesTempRepository
import io.olkkani.lfr.util.LostarkAPIClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.scheduler.Schedulers
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class AuctionGemService(
    private val repository: ItemPricesRepository,
    private val repositorySupport: ItemPricesRepositorySupport,
    private val tempRepository: ItemPricesTempRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) {
    private val logger = KotlinLogging.logger {}
    private val apiClient = LostarkAPIClient(apiKey)

    @Volatile
    var gems = mutableListOf<ItemTodayPrices>()

    init {
        val today = LocalDate.now()
        val gemList = collectGemInfoList
        gemList.forEach { gemInfo ->
            gems.add(ItemTodayPrices(itemCode = gemInfo.itemCode))

            // 오늘자 임시 데이터가 있다면 삽입
            val tempPrices: MutableMap<LocalDateTime, Int> = mutableMapOf()
            tempRepository.findByItemCodeAndRecordedDate(itemCode = gemInfo.itemCode, recordedDate = today)
                .forEach { tempGemPrices ->
                    tempPrices[tempGemPrices.endDate] = tempGemPrices.price
                }
            if (tempPrices.isNotEmpty()) {
                gems.find { gem -> gem.itemCode == gemInfo.itemCode }?.addTodayPrices(tempPrices)
            }
        }
    }

    fun clearTodayPricesRecord() {
        logger.info { "Clear Gems instance ${LocalDateTime.now().toString()}" }
        gems.clear()
        val gemList = collectGemInfoList
        gemList.forEach { gemInfo ->
            gems.add(ItemTodayPrices(itemCode = gemInfo.itemCode))
        }
    }

    suspend fun fetchGemPrices() = coroutineScope {
        val gemList = collectGemInfoList
        gemList.map { gemInfo ->
            async {
                try {
                    val response = apiClient.fetchAuctionItems(gemInfo.request)
                        .subscribeOn(Schedulers.boundedElastic())
                        .awaitSingle()

                    if (response != null && response.items.isNotEmpty()) {
                        val responseGemPrices = response.items.associate {
                            it.auctionInfo.endDate to it.auctionInfo.buyPrice
                        }
                        gems.find { it.itemCode == gemInfo.itemCode }?.addTodayPrices(responseGemPrices)
                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    fun updatePrevPriceTrend() {
        val gemList = collectGemInfoList
        val gemsPrices: MutableMap<Int, List<ItemPrices>> = mutableMapOf()
        gemList.forEach { gemInfo ->
            gemsPrices[gemInfo.itemCode] = repositorySupport.findPrevEightDaysPricesByItemCode(gemInfo.itemCode)
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

    fun saveTodayPricesToTemp() {
        gems.forEach { gem ->
            if (gem.todayPrices.isNotEmpty()) {
                tempRepository.saveAll(gem.toTempDomains())
            }
        }
    }

    fun getTodayTempPricesAndAddPrices() {
        gems.forEach { gem ->
            val prices = mutableMapOf<LocalDateTime, Int>()
            tempRepository.findByItemCodeAndRecordedDate(gem.itemCode, gem.time).let { tempItemPrices ->
                tempItemPrices.forEach { tempItemPrice ->
                    prices[tempItemPrice.endDate] = tempItemPrice.price
                }
            }
            if (prices.isNotEmpty()) {
                gem.addTodayPrices(prices)
            }
        }
    }

    fun getPricesByItemCode(itemCode: Int): List<ItemPrices> {
        val prices = repositorySupport.findOldAllByItemCode(itemCode)
        gems.find { it.itemCode == itemCode }?.let { prices.add(it.toDomain()) }
        return prices
    }

    fun getAllKindsTodayPrice(): MutableList<ItemTodayPrices> {
        return gems
    }

    fun getPriceTrendByItemCode(itemCode: Int): Map<LocalDate, PriceTrend> {
        return gems[itemCode].priceFiveDaysTrend
    }
}