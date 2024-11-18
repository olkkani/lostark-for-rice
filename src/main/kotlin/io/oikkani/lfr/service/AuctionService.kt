package io.oikkani.lfr.service

import io.oikkani.lfr.LostArkApiClient
import io.oikkani.lfr.model.AuctionRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class AuctionService {

    private val apiKey = ""
    private val apiClient = LostArkApiClient(apiKey)

    private val auctionRequests = listOf(
        "lv10Annihilation" to AuctionRequest(itemTier = 3, itemGrade = "유물", itemName = "10레벨 멸화의 보석"),
        "lv10CrimsonFlame" to AuctionRequest(itemTier = 3, itemGrade = "유물", itemName = "10레벨 홍염의 보석"),
        "lv8DoomFire" to AuctionRequest(itemTier = 4, itemGrade = "유물", itemName = "8레벨 겁화의 보석"),
        "lv8Blazing" to AuctionRequest(itemTier = 4, itemGrade = "유물", itemName = "8레벨 작열의 보석"),
        "lv10DoomFire" to AuctionRequest(itemTier = 4, itemGrade = "고대", itemName = "10레벨 겁화의 보석"),
        "lv10Blazing" to AuctionRequest(itemTier = 4, itemGrade = "고대", itemName = "10레벨 작열의 보석")
    )

    private val auctionPrices: MutableMap<String, MutableList<Long>> = mutableMapOf()

    init {
        // 초기화: 결과 저장소를 준비
        auctionRequests.forEach { (key, _) ->
            auctionPrices[key] = mutableListOf()
        }
    }

    @Scheduled(cron = "0 0 1-23/2 * * *", zone = "Asia/Seoul")
    fun auctionItemScheduler() {
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

    fun saveStatisticsGemPrices() {

    }

    fun saveAuctionPrices() {
        auctionPrices.forEach { (key, prices) ->
            val averagePrice = prices.average().toLong()
            println("$key: $averagePrice")
        }
    }

}