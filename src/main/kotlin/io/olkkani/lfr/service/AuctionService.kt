package io.olkkani.lfr.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.domain.*
import io.olkkani.lfr.dto.AuctionResponse
import io.olkkani.lfr.dto.gemsInfo
import io.olkkani.lfr.util.IQRCalculator
import io.olkkani.lfr.util.LostarkAPIClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class AuctionService(
    private val repository: ItemPricesRepository,
    private val repositorySupport: ItemPricesRepositorySupport,
    private val tempRepository: ItemPricesTempRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) {
    private val logger = KotlinLogging.logger {}
    private val apiClient = LostarkAPIClient(apiKey)

    private val auctionRequests = gemsInfo
    private val todayGemPrices: MutableMap<Int, MutableMap<LocalDateTime, Int>> = mutableMapOf()
    private val gemsOpenPrice: MutableMap<Int, Int> = mutableMapOf()


    init {
        val today = LocalDate.now()
        // 초기화: 결과 저장소를 준비
        auctionRequests.forEach { (key, _) ->
            todayGemPrices[key] = mutableMapOf()
            gemsOpenPrice[key] = 0
            // 이전에 종료된 데이터가 있다면 불러옴
            tempRepository.findByItemCodeAndRecordedDate(itemCode = key, recordedDate = today)
                .forEach { tempGemPrices ->
                    todayGemPrices[key]?.put(tempGemPrices.endDate, tempGemPrices.price)
                }
        }
    }

    fun clearTodayPrices() {
        auctionRequests.forEach { (key, _) ->
            todayGemPrices[key] = mutableMapOf()
            gemsOpenPrice[key] = 0
        }
    }

    fun putGemsPrices(itemCode: Int, auctionResponse: AuctionResponse) {
        auctionResponse.items.forEach {
            todayGemPrices[itemCode]?.getOrPut(it.auctionInfo.endDate) { it.auctionInfo.buyPrice }
        }
    }

    fun saveTodayGemsPricesTemp() {
        val today: LocalDate = LocalDate.now()
        todayGemPrices.forEach { (key, gemAuctionData) ->
            val prices: MutableList<ItemPricesTemp> = mutableListOf()
            gemAuctionData.forEach {
                ItemPricesTemp(
                    recordedDate = today, endDate = it.key, itemCode = key, price = it.value
                ).also { item ->
                    prices.add(item)
                }
            }
            tempRepository.saveAll(prices)
        }
    }

    fun saveTodayGemsPrices() {
        val today: LocalDate = LocalDate.now()
        // 4. 가져오고 싶은 매물의 개수만큼 작업을 반복
        todayGemPrices.forEach { (key, prices) ->
            var closePrice: Int = 0
            // 0. 현재 시세 가져오기
            apiClient.fetchAuctionItems(auctionRequests.first { it.first == key }.second).subscribe({ response ->
                // 1. 가져온 데이터를 전체 시세 목록에 반영
                putGemsPrices(itemCode = key, auctionResponse = response)
                // 2. 종가 가져오기
                val fetchPrices: List<Double> = response.items.map { it.auctionInfo.buyPrice.toDouble() }
                val iqrCalculatorForClosePrice = IQRCalculator(fetchPrices)
                closePrice = iqrCalculatorForClosePrice.getMin()?.toInt() ?: 0
            }, { error ->
                logger.error { "Error fetching $key: ${error.message}" }
            }, {
                // 3. 시작가, 최고가, 최저가 가져오기
                val iqrCalculator = IQRCalculator(prices.values.map { it.toDouble() })
                val lowPrice = iqrCalculator.getMin()?.toInt() ?: 0
                val highPrice = iqrCalculator.getMax()?.toInt() ?: 0
                val openPrice = gemsOpenPrice[key] ?: 0
                // 3. 최종 시세를 데이터베이스 저장
                ItemPrices(
                    recordedDate = today,
                    itemCode = key,
                    closePrice = if (closePrice == 0) lowPrice else closePrice,
                    openPrice = openPrice,
                    highPrice = highPrice,
                    lowPrice = lowPrice,
                ).also {
                    repository.save(it)
                }
            })
        }
    }


    fun fetchGemsPrice() {
        clearTodayPrices()
        auctionRequests.forEach { (key, request) ->
            apiClient.fetchAuctionItems(request).subscribe({ response ->
                // 1. 가져온 데이터를 전체 시세 목록에 반영
                putGemsPrices(itemCode = key, auctionResponse = response)
                // 2. Open Price 를 추가
                if (gemsOpenPrice[key] == 0 || gemsOpenPrice[key] == null) {
                    val fetchPrices: List<Double> = response.items.map { it.auctionInfo.buyPrice.toDouble() }
                    val iqrCalculator = IQRCalculator(fetchPrices)
                    gemsOpenPrice[key] = iqrCalculator.getMin()?.toInt() ?: 0
                }
            }, { error ->
                logger.error { "Error fetching $key: ${error.message}" }
            })
        }
    }

    fun fetchGemsPricesSync() {
        auctionRequests.forEach { (key, request) ->
            val response = apiClient.fetchAuctionItemsSynchronously(request)
            response?.let {
                putGemsPrices(itemCode = key, auctionResponse = it)
            }
        }
    }

    fun getAllPrices(): MutableMap<Int, List<ItemPrices>> {
        val today: LocalDate = LocalDate.now()

        if (todayGemPrices.entries.iterator().next().value.isEmpty()) {
            fetchGemsPricesSync()
        }

        val gemsPrices: MutableMap<Int, List<ItemPrices>> = mutableMapOf()
        auctionRequests.forEach { (key, _) ->
            val gemOldPrices: MutableList<ItemPrices> = repositorySupport.findOldAllByItemCode(key)

            val gemPrices = todayGemPrices[key]
            if (!gemPrices.isNullOrEmpty()){

                gemPrices.map { it.value.toDouble() }.let { gemTodayPrices ->
                    val iqrCalculator = IQRCalculator(gemTodayPrices)
                    val lowPrice = iqrCalculator.getMin().toInt()
                    val highPrice = iqrCalculator.getMax().toInt()
                    var openPrice: Int = gemsOpenPrice[key] ?: 0
                    openPrice = if (openPrice == 0) lowPrice else openPrice

                    ItemPrices(
                        closePrice = lowPrice,
                        openPrice = openPrice,
                        highPrice = highPrice,
                        lowPrice = lowPrice,
                        recordedDate = today,
                    )
                }.also {
                    gemOldPrices.add(it)
                    gemsPrices[key] = gemOldPrices
                }
            }
        }
            return gemsPrices
    }
}
