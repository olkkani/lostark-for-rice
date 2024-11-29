package io.olkkani.lfr.service

import io.olkkani.lfr.api.LostarkAPIClient
import io.olkkani.lfr.domain.*
import io.olkkani.lfr.model.gemsInfo
import io.olkkani.lfr.util.IQRCalculator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class AuctionService(
    private val repository: ItemPricesRepository,
    private val repositorySupport: ItemPricesRepositorySupport,
    private val tempRepository: ItemPricesTempRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
) {
    private val apiClient = LostarkAPIClient(apiKey)

    private val auctionRequests = gemsInfo
    private val gemsPrices: MutableMap<Int, MutableList<Int>> = mutableMapOf()
    private val gemsOpenPrice: MutableMap<Int, Int> = mutableMapOf()


    init {
        val today = LocalDate.now()
        // 초기화: 결과 저장소를 준비
        auctionRequests.forEach { (key, _) ->
            gemsPrices[key] = mutableListOf()
            gemsOpenPrice[key] = 0
            // 이전에 종료된 데이터가 있다면 불러옴
            tempRepository.findByItemCodeAndRecordedDate(itemCode = key, recordedDate = today).forEach{
                gemsPrices[key]?.add(it.price)
            }
        }

    }

    fun clearTodayPrices() {
        auctionRequests.forEach { (key, _) ->
            gemsPrices[key] = mutableListOf()
            gemsOpenPrice[key] = 0
        }
    }

    fun saveTodayGemsPricesTemp() {
        val today: LocalDate = LocalDate.now()
        gemsPrices.forEach { (gem, prices) ->
            val gemCode = auctionRequests.first { it.first == gem }.second.itemCode
            prices.forEach { price ->
                tempRepository.save(
                    ItemPricesTemp(
                        recordedDate = today,
                        itemCode = gemCode,
                        price = price
                    )
                )
            }
        }
    }

    fun saveTodayGemsPrices() {
        val today: LocalDate = LocalDate.now()
        // 5. 가져오고 싶은 매물의 개수만큼 작업을 반복
        gemsPrices.forEach { (key, prices) ->
            val gemCode = auctionRequests.first { it.first == key }.second.itemCode
            val pricesForClosePrice = mutableListOf<Int>()
            // 0. 현재 시세 가져오기
            apiClient.getAuctionItems(auctionRequests.first { it.first == key }.second)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            // 1. 가져온 항목을 list 로 담기
                            prices.add(item.auctionInfo.buyPrice)
                        }
                        // 1-1. 이상치를 제거한 최저값을 가져온다.(close price)
                        val closePrice: Int =
                            IQRCalculator(pricesForClosePrice.map { it.toDouble() }).getMin()!!.toInt()
                        // 2. 기존 시세에 현재 시세를 더한 후 이상치를 제거
                        prices.addAll(pricesForClosePrice)

                        val iqrCalculator = IQRCalculator(prices.map { it.toDouble() })
                        // 3. 준비된 값을 저장
                        repository.save(
                            ItemPrices(
                                recordedDate = today,
                                itemCode = gemCode,
                                closePrice = closePrice,
                                openPrice = gemsOpenPrice[key] ?: iqrCalculator.getMin()!!.toInt(),
                                highPrice = iqrCalculator.getMax()!!.toInt(),
                                lowPrice = iqrCalculator.getMin()!!.toInt(),
                            )
                        )
                    },
                    { error ->
                        println("Error fetching $key: ${error.message}")
                    }
                )
        }
    }


    fun getTodayGemsPricesTemp() {
        val today: LocalDate = LocalDate.now()
        auctionRequests.forEach { (gem, gemInfo) ->
            tempRepository.findByItemCodeAndRecordedDate(itemCode = gemInfo.itemCode, recordedDate = today)
                .forEach { response ->
                    gemsPrices[gem]?.add(response.price)
                }

        }
    }

    fun getGemOpenPrices() {
        clearTodayPrices()
        auctionRequests.forEach { (key, request) ->
            apiClient.getAuctionItems(request)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            gemsPrices[key]?.add(item.auctionInfo.buyPrice)
                        }
                        gemsOpenPrice[key] =
                            IQRCalculator(gemsPrices[key]!!.map { it.toDouble() }).getMin()!!.toInt()
                    },
                    { error ->
                        println("Error fetching $key: ${error.message}")
                    }
                )
        }
    }

    fun fetchingGemsPrices() {
        auctionRequests.forEach { (key, request) ->
            apiClient.getAuctionItems(request)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            gemsPrices[key]?.add(item.auctionInfo.buyPrice)
                        }
                    },
                    { error ->
                        println("Error fetching $key: ${error.message}")
                    }
                )
        }
    }

    fun getAllGemsPricesByItemCode(itemCode: Int): MutableList<ItemPrices> {
        val today: LocalDate = LocalDate.now()
        // Gems prices 가져오기
        val prices = gemsPrices[itemCode]?.map { it.toDouble() }
            ?: throw IllegalArgumentException("Invalid item code: $itemCode")

        val iqrCalculator = IQRCalculator(prices)
        val lowPrice = iqrCalculator.getMin()!!.toInt()
        val highPrice = iqrCalculator.getMax()!!.toInt()

        val itemPrices = ItemPrices(
            // TODO: 현재가와 오늘의 최저가가 동일함, 가장 최근에 불러온 데이터는 따로 관리 후 저장
            closePrice = lowPrice,
            openPrice = gemsOpenPrice[itemCode] ?: 0,
            highPrice = highPrice,
            lowPrice = lowPrice,
            recordedDate = today,
        )
        val gemPrices: MutableList<ItemPrices> = repositorySupport.findOldAllByItemCode(itemCode)
        gemPrices.add(itemPrices)
        return gemPrices
    }
}