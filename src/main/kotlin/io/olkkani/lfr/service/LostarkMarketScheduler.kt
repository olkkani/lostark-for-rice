package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.MarketRequest
import io.olkkani.lfr.dto.toMarketTodayPrice
import io.olkkani.lfr.entity.jpa.MarketPriceIndex
import io.olkkani.lfr.repository.jpa.MarketPriceIndexRepo
import io.olkkani.lfr.repository.mongo.MarketTodayPriceMongoRepo
import io.olkkani.lfr.util.LostarkAPIClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LostarkMarketScheduler(
    private val indexRepo: MarketPriceIndexRepo,
    private val todayPriceRepo: MarketTodayPriceMongoRepo,
    private val apiClient: LostarkAPIClient,
) {
    private val logger = KotlinLogging.logger {}
    private val abidos: MarketRequest = MarketRequest(50010, "아비도스 융화 재료")

    @Transactional
    suspend fun fetchPriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean = false): Unit = coroutineScope {
        async {
            try {
                val response = apiClient.fetchMarketItemPriceSubscribe(abidos)
                if (response != null) {
                    // save today prices
                    todayPriceRepo.saveIfNotExists(response.toMarketTodayPrice())
                    // update today price index
                    val todayPrices = todayPriceRepo.findPricesByItemCode(response.items.id).map { it.getPrice() }
                    val savedTodayPriceIndex =
                        indexRepo.findByItemCodeAndRecordedDate(itemCode = response.items.id, recordedDate = LocalDate.now())
                    savedTodayPriceIndex?.apply {
                        savedTodayPriceIndex.closePrice = response.items.currentMinPrice
                        savedTodayPriceIndex.highPrice = todayPrices.max()
                        savedTodayPriceIndex.lowPrice = todayPrices.min()
                    }?.also {
                        indexRepo.save(it)
                    } ?: run {
                        indexRepo.save(MarketPriceIndex(
                            itemCode = response.items.id,
                            recordedDate = LocalDate.now(),
                            openPrice = response.items.currentMinPrice,
                            lowPrice = todayPrices.min(),
                            highPrice = todayPrices.max(),
                            closePrice = response.items.currentMinPrice
                        ))
                    }

                    if(isUpdateYesterdayAvgPrice){
                        // add yesterday avg price
                        val yesterdayIndex = indexRepo.findByItemCodeAndRecordedDate(itemCode = response.items.id, recordedDate = LocalDate.now().minusDays(1))
                        yesterdayIndex?.apply {
                            avgPrice = response.items.yDayAvgPrice
                        }?.also {
                            indexRepo.save(it)
                        }
                    }
                }
            } catch (error: Exception) {
                logger.error { "Error fetching ${abidos.itemName}: ${error.message}" }
            }
        }.await()
    }

     fun clearOldPriceRecord() {
        todayPriceRepo.deleteAll()
    }
}