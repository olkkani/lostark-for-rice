package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.dto.extractPrices
import io.olkkani.lfr.dto.toTodayItemPrices
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.entity.mongo.ItemPriceIndexTrend
import io.olkkani.lfr.entity.mongo.PriceRecord
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import io.olkkani.lfr.repository.mongo.ItemPriceIndexTrendRepository
import io.olkkani.lfr.repository.mongo.TodayItemPriceRepository
import io.olkkani.lfr.util.IQRCalculator
import io.olkkani.lfr.util.LostarkAPIClient
import io.olkkani.lfr.util.PercentageCalculation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
class AuctionSchedulerServiceImpl(
    val indexRepository: ItemPriceIndexRepository,
    val todayPriceRepository: TodayItemPriceRepository,
    val indexTrendRepository: ItemPriceIndexTrendRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) : AuctionSchedulerService {
    private val logger = KotlinLogging.logger {}
    private val apiClient = LostarkAPIClient(apiKey)

    override suspend fun fetchPriceAndInsertOpenPrice(): Unit = coroutineScope {
        val today = LocalDate.now()
        val gemList = collectGemInfoList
        gemList.map { gemInfo ->
            async {
                try {
                    val response = apiClient.fetchAuctionItemsSubscribe(gemInfo.request)
                    if (response.items.isNotEmpty()) {
                        val fetchedPrices = response.toTodayItemPrices(itemCode = gemInfo.itemCode)
                        todayPriceRepository.saveIfNotExists(fetchedPrices)

                        val iqrCal = IQRCalculator(response.extractPrices())
                        indexRepository.save(
                            ItemPriceIndex(
                                itemCode = gemInfo.itemCode,
                                recordedDate = today,
                                openPrice = iqrCal.getMin(),
                                lowPrice = iqrCal.getMin(),
                                highPrice = iqrCal.getMax(),
                                closePrice = iqrCal.getMin()
                            )
                        )
                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    @Transactional
    override suspend fun fetchPriceAndUpdateClosePrice(): Unit = coroutineScope {
        val today = LocalDate.now()
        val gemList = collectGemInfoList
        gemList.map { gemInfo ->
            async {
                try {
                    val response = apiClient.fetchAuctionItemsSubscribe(gemInfo.request)
                    if (response.items.isNotEmpty()) {
                        val fetchedPrices = response.toTodayItemPrices(itemCode = gemInfo.itemCode)
                        todayPriceRepository.saveIfNotExists(fetchedPrices)

                        val iqrCal = IQRCalculator(response.extractPrices())
                        val savedTodayPriceIndex =
                            indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today)

                        savedTodayPriceIndex?.apply {
                            savedTodayPriceIndex.closePrice = iqrCal.getMin()
                        }?.also {
                            indexRepository.save(it)
                        } ?: run {
                            val savedTodayPrices =
                                todayPriceRepository.findPricesByItemCode(gemInfo.itemCode).map { it.getPrice() }
                            val todayIqrCal = IQRCalculator(savedTodayPrices)
                            indexRepository.save(
                                ItemPriceIndex(
                                    itemCode = gemInfo.itemCode,
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
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    @Transactional
    override suspend fun fetchPriceAndUpdatePrice(): Unit = coroutineScope {
        val today = LocalDate.now()
        val gemList = collectGemInfoList
        gemList.map { gemInfo ->
            async {
                try {
                    val response = apiClient.fetchAuctionItemsSubscribe(gemInfo.request)
                    if (response.items.isNotEmpty()) {
                        val fetchedPrices = response.toTodayItemPrices(itemCode = gemInfo.itemCode)
                        todayPriceRepository.saveIfNotExists(fetchedPrices)

                        val savedTodayPrices =
                            todayPriceRepository.findPricesByItemCode(gemInfo.itemCode).map { it.getPrice() }
                        val iqrCal = IQRCalculator(response.extractPrices())
                        val todayIqrCal = IQRCalculator(savedTodayPrices)
                        val savedTodayPriceIndex =
                            indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today)

                        savedTodayPriceIndex?.apply {

                            lowPrice = todayIqrCal.getMin()
                            closePrice = iqrCal.getMin()
                            highPrice = todayIqrCal.getMax()
                        }?.also {
                            indexRepository.save(it)
                        } ?: run {
                            indexRepository.save(
                                ItemPriceIndex(
                                    itemCode = gemInfo.itemCode,
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
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    override fun clearOldPriceRecord() {
        todayPriceRepository.deleteAll()

        val prevTenDays = LocalDate.now().minusDays(10)
        val indexRecords = indexTrendRepository.findAll()
        indexRecords.forEach {
            it.priceRecords = it.priceRecords.filter { record -> record.date > prevTenDays }.toMutableList()
        }
        indexTrendRepository.saveAll(indexRecords)
    }

    override fun calculateGapTodayItemPrice() {
        val today = LocalDate.now()
        val gemList = collectGemInfoList
        gemList.map { gemInfo ->
            val todayIndex = indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today)
            val todayPairIndex = indexRepository.findByItemCodeAndRecordedDate(gemInfo.pairItemCode, today)
            val yesterdayIndex = indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today.minusDays(1))
            val indexTrend = indexTrendRepository.findByItemCode(gemInfo.itemCode)

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
                            PriceRecord(
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
                        ItemPriceIndexTrend(
                            itemCode = gemInfo.itemCode,
                            priceRecords = mutableListOf(
                                PriceRecord(
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