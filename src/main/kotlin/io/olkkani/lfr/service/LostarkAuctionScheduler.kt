package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dao.GemDAO
import io.olkkani.lfr.dao.toRequest
import io.olkkani.lfr.dto.extractPrices
import io.olkkani.lfr.dto.toTodayItemPrices
import io.olkkani.lfr.entity.jpa.AuctionPriceIndex
import io.olkkani.lfr.entity.mongo.TodayPriceGap
import io.olkkani.lfr.entity.mongo.RecentPriceIndexTrend
import io.olkkani.lfr.repository.jpa.AuctionPriceIndexRepo
import io.olkkani.lfr.repository.mongo.RecentPriceIndexTrendMongoRepo
import io.olkkani.lfr.repository.mongo.TodayItemPriceRepository
import io.olkkani.lfr.util.IQRCalculator
import io.olkkani.lfr.util.LostarkAPIClient
import io.olkkani.lfr.util.PercentageCalculation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LostarkAuctionScheduler(
    val indexRepository: AuctionPriceIndexRepo,
    val todayPriceRepository: TodayItemPriceRepository,
    val indexTrendRepository: RecentPriceIndexTrendMongoRepo,
    private val apiClient: LostarkAPIClient
) {
    private val logger = KotlinLogging.logger {}

    val gemDAO = listOf(
        GemDAO(itemCode = 65021100, pairItemCode = 65022100, name = "10레벨 멸화의 보석"),
        GemDAO(itemCode = 65022100, pairItemCode = 65021100, name = "10레벨 홍염의 보석"),
        GemDAO(itemCode = 65031080, pairItemCode = 65032080, name = "8레벨 겁화의 보석"),
        GemDAO(itemCode = 65032080, pairItemCode = 65031080, name = "8레벨 작열의 보석"),
        GemDAO(itemCode = 65031100, pairItemCode = 65032100, name = "10레벨 겁화의 보석"),
        GemDAO(itemCode = 65032100, pairItemCode = 65031100, name = "10레벨 작열의 보석")
    )

    @Transactional
    suspend fun fetchPriceAndUpdatePrice(): Unit = coroutineScope {
        val today = LocalDate.now()
        gemDAO.map { gem ->
            async {
                try {
                    val response = apiClient.fetchAuctionItemsSubscribe(gem.toRequest())
                    if (response.items.isNotEmpty()) {
                        val fetchedPrices = response.toTodayItemPrices(itemCode = gem.itemCode)
                        // save today prices
                        todayPriceRepository.saveIfNotExists(fetchedPrices)
                        // update auction today price index
                        val savedTodayPrices =
                            todayPriceRepository.findPricesByItemCode(gem.itemCode).map { it.getPrice() }
                        val iqrCal = IQRCalculator(response.extractPrices())
                        val todayIqrCal = IQRCalculator(savedTodayPrices)
                        val savedTodayPriceIndex =
                            indexRepository.findByItemCodeAndRecordedDate(gem.itemCode, today)
                        savedTodayPriceIndex?.apply {
                            highPrice = todayIqrCal.getMax()
                            lowPrice = todayIqrCal.getMin()
                            closePrice = iqrCal.getMin()
                        }?.also {
                            indexRepository.save(it)
                        }?: run {
                            indexRepository.save(
                                AuctionPriceIndex(
                                    itemCode = gem.itemCode,
                                    recordedDate = today,
                                    openPrice = todayIqrCal.getMin(),
                                    lowPrice = todayIqrCal.getMin(),
                                    highPrice = todayIqrCal.getMax(),
                                    closePrice = iqrCal.getMin()
                                )
                            )
                        }
                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gem.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
        calculateGapTodayItemPrice()
    }

    fun clearOldPriceRecord() {
        todayPriceRepository.deleteAll()

        val prevTenDays = LocalDate.now().minusDays(10)
        val indexRecords = indexTrendRepository.findAll()
        indexRecords.forEach {
            it.priceRecords = it.priceRecords.filter { record -> record.date > prevTenDays }.toMutableList()
        }
        indexTrendRepository.saveAll(indexRecords)
    }

    fun calculateGapTodayItemPrice() {
        val today = LocalDate.now()
        gemDAO.map { gem ->
            val todayIndex = indexRepository.findByItemCodeAndRecordedDate(gem.itemCode, today)
            val todayPairIndex = indexRepository.findByItemCodeAndRecordedDate(gem.pairItemCode, today)
            val yesterdayIndex = indexRepository.findByItemCodeAndRecordedDate(gem.itemCode, today.minusDays(1))
            val indexTrend = indexTrendRepository.findByItemCode(gem.itemCode)

            if (todayIndex != null && todayPairIndex != null && yesterdayIndex != null) {
                val prevGep = todayIndex.closePrice - yesterdayIndex.closePrice
                val prevGapRate = PercentageCalculation().calc(todayIndex.closePrice, yesterdayIndex.closePrice)
                val pairGap = todayIndex.closePrice - todayPairIndex.closePrice
                val pairGapRate = PercentageCalculation().calc(todayIndex.closePrice, todayPairIndex.closePrice)

                indexTrend?.let { trend ->
                    trend.priceRecords.find { record -> record.date == today }
                        ?.apply {
                            // 기존 데이터가 있다면 변경 후 저장
                            price = todayIndex.closePrice
                            prevGapPrice = prevGep
                            prevGapPriceRate = prevGapRate
                            pairGapPrice = pairGap
                            pairGapPriceRate = pairGapRate
                        } ?: run {
                        // 오늘자 데이터가 없다면 오늘자 기록을 새로 추가
                        trend.priceRecords.add(
                            TodayPriceGap(
                                date = today,
                                price = todayIndex.closePrice,
                                prevGapPrice = prevGep,
                                prevGapPriceRate = prevGapRate,
                                pairGapPrice = pairGap,
                                pairGapPriceRate = pairGapRate
                            )
                        )
                    }
                    indexTrendRepository.save(trend)
                } ?: run {
                    // 전체 기록이 없다면 새로 생성 후 저장
                    indexTrendRepository.save(
                        RecentPriceIndexTrend(
                            itemCode = gem.itemCode,
                            priceRecords = mutableListOf(
                                TodayPriceGap(
                                    date = today,
                                    price = todayIndex.closePrice,
                                    prevGapPrice = prevGep,
                                    prevGapPriceRate = prevGapRate,
                                    pairGapPrice = pairGap,
                                    pairGapPriceRate = pairGapRate
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}