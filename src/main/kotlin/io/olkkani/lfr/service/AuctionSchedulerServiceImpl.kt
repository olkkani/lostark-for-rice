package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.collectGemInfoList
import io.olkkani.lfr.dto.extractPrices
import io.olkkani.lfr.dto.toTodayItemPrices
import io.olkkani.lfr.entity.jpa.ItemPriceIndex
import io.olkkani.lfr.repository.jpa.ItemPriceIndexRepository
import io.olkkani.lfr.repository.mongo.TodayItemPriceRepository
import io.olkkani.lfr.util.IQRCalculator
import io.olkkani.lfr.util.LostarkAPIClient
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
                        val savedTodayPrice = indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today)
                        if (savedTodayPrice != null) {
                            savedTodayPrice.closePrice = iqrCal.getMin()
                        } else {
                            fetchPriceAndInsertOpenPrice()
                        }
                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    @Transactional
    override suspend fun fetchPriceAndUpdateLowAndHighPrice(): Unit = coroutineScope {
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
                        val iqrCal = IQRCalculator(savedTodayPrices)
                        val savedTodayPriceIndex =
                            indexRepository.findByItemCodeAndRecordedDate(gemInfo.itemCode, today)

                        if (savedTodayPriceIndex != null) {
                            savedTodayPriceIndex.lowPrice = iqrCal.getMin()
                            savedTodayPriceIndex.highPrice = iqrCal.getMax()
                        } else {
                            fetchPriceAndInsertOpenPrice()
                        }

                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gemInfo.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
    }

    override fun clearTodayPriceRecord() {
        todayPriceRepository.deleteAll()
    }
}