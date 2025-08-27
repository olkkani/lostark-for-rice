package io.oikkani.integrationservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.adapter.external.dao.AuctionDAO
import io.olkkani.lfr.adapter.external.LostarkAPIClient
import io.olkkani.lfr.adapter.external.dto.extractPrices
import io.olkkani.lfr.common.util.IQRCalculator
import io.olkkani.lfr.common.util.PercentageCalculation
import io.olkkani.lfr.repository.AuctionItemPriceSnapshotRepo
import io.olkkani.lfr.repository.DailyAuctionItemOhlcPriceRepo
import io.olkkani.lfr.repository.ItemPreviousPriceChangeRepo
import io.olkkani.lfr.repository.entity.DailyAuctionItemOhlcPrice
import io.olkkani.lfr.repository.entity.ItemPreviousPriceChange
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface LostarkAuctionScheduler{
    fun updateTodayHlcPrice(itemCode: Int, currentPrice: Int)
    fun calculateGapTodayItemPrice()
    suspend fun fetchPriceAndUpdatePrice()
}
@Service
class LostarkAuctionSchedulerImpl(
    private val ohlcPriceRepo: DailyAuctionItemOhlcPriceRepo,
    private val todayPriceRepo: AuctionItemPriceSnapshotRepo,
    private val itemPreviousPriceChangeRepo: ItemPreviousPriceChangeRepo,
    @Qualifier("auctionAPIClient") private val apiClient: LostarkAPIClient,
): LostarkAuctionScheduler {
    private val logger = KotlinLogging.logger {}

    val gemDAO = listOf(
        AuctionDAO(itemCode = 65021100, pairItemCode = 65022100, itemName = "10레벨 멸화의 보석"),
        AuctionDAO(itemCode = 65022100, pairItemCode = 65021100, itemName = "10레벨 홍염의 보석"),
        AuctionDAO(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석"),
        AuctionDAO(itemCode = 65032080, pairItemCode = 65031080, itemName = "8레벨 작열의 보석"),
        AuctionDAO(itemCode = 65031100, pairItemCode = 65032100, itemName = "10레벨 겁화의 보석"),
        AuctionDAO(itemCode = 65032100, pairItemCode = 65031100, itemName = "10레벨 작열의 보석")
    )

    @Transactional
    override suspend fun fetchPriceAndUpdatePrice(): Unit = coroutineScope {
        gemDAO.map { gem ->
            async {
                try {
                    val response = apiClient.fetchAuctionItemsSubscribe(gem.toGemRequest())
                    if (response.items.isNotEmpty()) {
                        //  1. Save Item Now Price
                        val fetchedPrices = response.toEntity(itemCode = gem.itemCode)
                        todayPriceRepo.saveAllIgnoreDuplicates(fetchedPrices)
                        // 2. Update Today HLC Price
                        val iqrCal = IQRCalculator(response.extractPrices())
                        updateTodayHlcPrice(itemCode = gem.itemCode, currentPrice = iqrCal.getMin())
                    }
                } catch (error: Exception) {
                    logger.error { "Error fetching ${gem.itemCode}: ${error.message}" }
                }
            }
        }.awaitAll()
        calculateGapTodayItemPrice()
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun calculateGapTodayItemPrice() {
        val today = LocalDate.now()
        // TODO: 중복으로 가져오는 경우를 해결하기 위해 고민
        gemDAO.map { gem ->
            val todayIndex = ohlcPriceRepo.findByItemCodeAndRecordedDate(gem.itemCode, today)
            val todayPairIndex = ohlcPriceRepo.findByItemCodeAndRecordedDate(gem.pairItemCode, today)
            val yesterdayIndex = ohlcPriceRepo.findByItemCodeAndRecordedDate(gem.itemCode, today.minusDays(1))

            if (todayIndex != null && todayPairIndex != null && yesterdayIndex != null) {
                val prevGep = todayIndex.closePrice - yesterdayIndex.closePrice
                val prevGapRate = PercentageCalculation().calc(todayIndex.closePrice, yesterdayIndex.closePrice)
                val pairGap = todayIndex.closePrice - todayPairIndex.closePrice
                val pairGapRate = PercentageCalculation().calc(todayIndex.closePrice, todayPairIndex.closePrice)

                itemPreviousPriceChangeRepo
                    .findByItemCodeAndRecordedDate(gem.itemCode, today)
                    ?.apply {
                        // 기존 데이터가 존재하면 오늘자 데이터를 수정
                        price = todayIndex.closePrice
                        priceDiffPrevDay = prevGep
                        priceDiffRatePrevDay = prevGapRate
                        priceDiffPairItem = pairGap
                        priceDiffRatePairItem = pairGapRate
                    } ?: run {
                    // 데이터가 존재하지 않으면 신규 데이터를 삽입
                    itemPreviousPriceChangeRepo.save(
                        ItemPreviousPriceChange(
                            itemCode = gem.itemCode,
                            recordedDate = today,
                            price = todayIndex.closePrice,
                            priceDiffPrevDay = prevGep,
                            priceDiffRatePrevDay = prevGapRate,
                            priceDiffPairItem = pairGap,
                            priceDiffRatePairItem = pairGapRate
                        )
                    )
                }
            }
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateTodayHlcPrice(itemCode: Int, currentPrice: Int) {
        val today = LocalDate.now()
        val priceRange = todayPriceRepo.findFilteredPriceRangeByItemCode(itemCode)
        ohlcPriceRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)?.apply {
            highPrice = priceRange.max
            lowPrice = priceRange.min
            closePrice = currentPrice
            ohlcPriceRepo.save(this)
        } ?: run {
            ohlcPriceRepo.save(
                DailyAuctionItemOhlcPrice(
                    itemCode = itemCode,
                    recordedDate = today,
                    openPrice = priceRange.min,
                    highPrice = priceRange.max,
                    lowPrice = priceRange.min,
                    closePrice = currentPrice
                )
            )
        }
    }
}