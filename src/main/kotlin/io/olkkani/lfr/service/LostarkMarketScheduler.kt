package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dao.RelicEngravingRecipeDAO
import io.olkkani.lfr.dto.MarketRequest
import io.olkkani.lfr.entity.DailyMarketItemOhlcaPrice
import io.olkkani.lfr.repository.DailyMarketItemOhlcaPriceRepo
import io.olkkani.lfr.repository.ItemPreviousPriceChangeRepo
import io.olkkani.lfr.repository.MarketItemPriceSnapshotRepo
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


interface LostarkMarketScheduler {
    fun updateTodayHlcPrice(itemCode: Int, currentPrice: Int)
    fun updateYesterdayAvgPrice(itemCode: Int, yesterdayAvgPrice: Float)
    suspend fun fetchMaterialPriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean = false)
    suspend fun fetchEngravingRecipePriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean = false)
}

@Service
class LostarkMarketSchedulerImpl(
    private val ohlcPriceRepo: DailyMarketItemOhlcaPriceRepo,
    private val priceSnapshotRepo: MarketItemPriceSnapshotRepo,
    private val itemPreviousPriceChangeRepo: ItemPreviousPriceChangeRepo,
    @Qualifier("marketAPIClient") private val apiClient: LostarkAPIClient,
) : LostarkMarketScheduler {
    private val logger = KotlinLogging.logger {}


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateTodayHlcPrice(itemCode: Int, currentPrice: Int) {
        val today = LocalDate.now()
        val priceRange = priceSnapshotRepo.findFilteredPriceRangeByItemCode(itemCode)
        ohlcPriceRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)?.apply {
            highPrice = priceRange.max
            lowPrice = priceRange.min
            closePrice = currentPrice
        } ?: run {
            ohlcPriceRepo.save(
                DailyMarketItemOhlcaPrice(
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun updateYesterdayAvgPrice(itemCode: Int, yesterdayAvgPrice: Float) {
        val yesterday = LocalDate.now().minusDays(1)
        ohlcPriceRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = yesterday)?.apply {
            avgPrice = yesterdayAvgPrice
        }
    }

    @Transactional
    override suspend fun fetchMaterialPriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean) {
        val abidos = MarketRequest(50010, "아비도스 융화 재료")
        try {
            val response = apiClient.fetchMarketItemPriceSubscribe(abidos)?.items?.firstOrNull()
            if (response != null) {
                // 1. Save Market Item Now Price
                priceSnapshotRepo.saveIgnoreDuplicates(response.toEntity())
                // 2. Update Today HLC Price
                updateTodayHlcPrice(response.id, response.currentMinPrice)
                // 3. 어제 평균 가격 업데이트 (필요한 경우)
                if (isUpdateYesterdayAvgPrice) {
                    updateYesterdayAvgPrice(response.id, response.yDayAvgPrice)
                }
            }
        } catch (error: Exception) {
            logger.error { "Error in fetchPriceAndUpdatePrice: ${error.message}" }
//             재시도 로직 이외의 예외는 트랜잭션을 롤백하기 위해 다시 던짐
            if (error !is OptimisticLockingFailureException) {
                throw error
            }
        }
    }

    @Transactional
    override suspend fun fetchEngravingRecipePriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean) {
        var pageNo = 1
        while (true) {
            val relicEngravingRecipeDAO = RelicEngravingRecipeDAO().toRequest(pageNo++)
            try {
                val response = apiClient.fetchMarketItemPriceSubscribe(relicEngravingRecipeDAO)?.items
                if (!response.isNullOrEmpty()) {
                    // 1. Save Market Item Now Price
                    priceSnapshotRepo.saveAllIgnoreDuplicates(response.map { it.toEntity() })
                    response.forEach { item ->
                        // 2. Update Today HLC Price
                        updateTodayHlcPrice(item.id, item.currentMinPrice)
                        // 3. 어제 평균 가격 업데이트 (필요한 경우)
                        if (isUpdateYesterdayAvgPrice) {
                            updateYesterdayAvgPrice(item.id, item.yDayAvgPrice)
                        }
                    }
                    if (response.size < 10) break
                }
            } catch (error: Exception) {
                logger.error { "Error in fetchPriceAndUpdatePrice: ${error.message}" }
//             재시도 로직 이외의 예외는 트랜잭션을 롤백하기 위해 다시 던짐
                if (error !is OptimisticLockingFailureException) {
                    throw error
                }
            }
        }
    }
}