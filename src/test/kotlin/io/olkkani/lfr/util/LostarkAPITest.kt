package io.olkkani.lfr.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.api.LostarkAPIClient
import io.olkkani.lfr.model.AuctionRequest
import org.junit.jupiter.api.Test

class LostarkAPITest (
){
    var apiKey = ""
    val apiClient = LostarkAPIClient(apiKey)
    private val logger = KotlinLogging.logger {}

    @Test
    fun `API_가져오기_TEST` (){
        // given
        val request = AuctionRequest(itemName = "10레벨 멸화의 보석", itemTier = 3, itemGrade = "유물", itemCode = 65021100)
        apiClient.getAuctionItems2(request)

        apiClient.getAuctionItems(request).subscribe({response ->
            response.items.forEach { item ->
                logger.error { item.auctionInfo.buyPrice }
            }
        })
    }
}