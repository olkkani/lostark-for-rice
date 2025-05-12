package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.dto.MarketRequest
import io.olkkani.lfr.dto.toMarketTodayPrice
import io.olkkani.lfr.entity.jpa.MarketPriceIndex
import io.olkkani.lfr.repository.jpa.MarketPriceIndexRepo
import io.olkkani.lfr.repository.mongo.MarketTodayPriceMongoRepo
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
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
    suspend fun fetchPriceAndUpdatePrice(isUpdateYesterdayAvgPrice: Boolean = false): Unit {
        try {
            val response = apiClient.fetchMarketItemPriceSubscribe(abidos)?.items?.firstOrNull()
            if (response != null) {
                // 1. 오늘 가격 저장
                todayPriceRepo.saveIfNotExists(response.toMarketTodayPrice())

                // 2. 오늘 가격 인덱스 업데이트
                val todayPrices = todayPriceRepo.findPricesByItemCode(response.id).map { it.getPrice() }
                updateTodayPriceIndex(response.id, response.currentMinPrice, todayPrices)

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateTodayPriceIndex(itemCode: Int, currentPrice: Int, todayPrices: List<Int>) {
        val today = LocalDate.now()
        indexRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = today)?.apply {
            closePrice = currentPrice
            highPrice = todayPrices.max()
            lowPrice = todayPrices.min()
        } ?: run {
            indexRepo.save(
                MarketPriceIndex(
                    itemCode = itemCode,
                    recordedDate = today,
                    openPrice = currentPrice,
                    lowPrice = todayPrices.min(),
                    highPrice = todayPrices.max(),
                    closePrice = currentPrice
                )
            )
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateYesterdayAvgPrice(itemCode: Int, yesterdayAvgPrice: Float) {
        val yesterday = LocalDate.now().minusDays(1)
        indexRepo.findByItemCodeAndRecordedDate(itemCode = itemCode, recordedDate = yesterday)?.apply {
            avgPrice = yesterdayAvgPrice
        }
    }

    @Transactional
    fun clearOldPriceRecord() {
        todayPriceRepo.deleteAll()
    }
}