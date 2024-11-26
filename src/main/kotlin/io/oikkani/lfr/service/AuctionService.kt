package io.oikkani.lfr.service

import io.oikkani.lfr.api.LostarkAPIClient
import io.oikkani.lfr.domain.ItemPrices
import io.oikkani.lfr.domain.ItemPricesRepository
import io.oikkani.lfr.domain.ItemPricesTemp
import io.oikkani.lfr.domain.ItemPricesTempRepository
import io.oikkani.lfr.model.gemsInfo
import io.oikkani.lfr.util.IQRCalculator
import io.oikkani.lfr.util.createTsid
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuctionService (
    private val repository: ItemPricesRepository,
    private val tempRepository: ItemPricesTempRepository,
    @Value("\${lostark.api.key}") private val apiKey: String
){
    private val apiClient = LostarkAPIClient(apiKey)

    private val auctionRequests = gemsInfo
    private val auctionPrices: MutableMap<String, MutableList<Int>> = mutableMapOf()
    private val auctionOpenPrice: MutableMap<String, Int> = mutableMapOf()

    init {
        // 초기화: 결과 저장소를 준비
        auctionRequests.forEach { (key, _) ->
            auctionPrices[key] = mutableListOf()
            auctionOpenPrice[key] = 0
        }
    }
    fun saveTodayGemsPricesTemp () {
        val today: LocalDate = LocalDate.now()
        auctionPrices.forEach { gem, prices ->
            val gemCode = auctionRequests.first{it.first == gem}.second.itemCode
            prices.forEach { price ->
               tempRepository.save(
                   ItemPricesTemp(
                       id = createTsid(),
                       recordedDate = today,
                       itemCode = gemCode,
                       price = price
                   )
               )
           }
        }
    }

    fun saveTodayGemsPrices () {
        val today: LocalDate = LocalDate.now()
        // 5. 가져오고 싶은 매물의 개수만큼 작업을 반복
        auctionPrices.forEach { key, prices ->
            val gemCode = auctionRequests.first{it.first == key}.second.itemCode
            val pricesForClosePrice = mutableListOf<Int>()
            // 현재 시세 가져오기
            apiClient.getAuctionItems(auctionRequests.first{it.first == key}.second)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            // 1. 가져온 항목을 list 로 담기
                            prices.add(item.auctionInfo.buyPrice)
                        }
                        // 1-1. 이상치를 제거한 최저값을 가져온다.(close price)
                        val closePrice: Int = IQRCalculator(pricesForClosePrice.map{it.toDouble()}).getMin()!!.toInt()
                        // 2. 기존 시세에 현재 시세를 더한 후 이상치를 제거
                        prices.addAll(pricesForClosePrice)

                        val iqrCalculator = IQRCalculator(prices.map{it.toDouble()})
                        // 3. 준비된 값을 저장
                        repository.save(
                            ItemPrices(
                                id = createTsid(),
                                recordedDate = today,
                                itemCode = gemCode,
                                closePrice = closePrice,
                                openPrice = auctionOpenPrice[key]?: iqrCalculator.getMin()!!.toInt(),
                                highPrice = iqrCalculator.getMax()!!.toInt() ,
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


    fun getTodayGemsPricesTemp () {
        val today: LocalDate = LocalDate.now()
        auctionRequests.forEach { (gem, gemInfo) ->
            tempRepository.findByItemCodeAndRecordedDate(itemCode = gemInfo.itemCode, recordedDate = today).forEach { response ->
                    auctionPrices[gem]?.add(response.price)
                }

        }
    }

    fun getGemOpenPrices() {
        auctionRequests.forEach { (key, request) ->
            apiClient.getAuctionItems(request)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            auctionPrices[key]?.add(item.auctionInfo.buyPrice)
                        }
                        auctionOpenPrice[key] =
                            IQRCalculator(auctionPrices[key]!!.map { it.toDouble() }).getMin()!!.toInt()
                    },
                    { error ->
                        println("Error fetching $key: ${error.message}")
                    }
                )
        }
    }

    fun getGemPrices() {
        auctionRequests.forEach { (key, request) ->
            apiClient.getAuctionItems(request)
                .subscribe(
                    { response ->
                        response.items.forEach { item ->
                            auctionPrices[key]?.add(item.auctionInfo.buyPrice)
                        }
                    },
                    { error ->
                        println("Error fetching $key: ${error.message}")
                    }
                )
        }
    }

    fun getGemsPrices() {
//        auctionRequests.forEach { (_, gem) ->
//            val oldPrices = repository.findAllByItemCode(gem.itemCode)
//            return oldPrices
//
//        }
    }
}