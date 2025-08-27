package io.oikkani.integrationservice.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.`in`.AuctionGemUseCase
import io.oikkani.integrationservice.application.port.out.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.out.client.AuctionClient
import io.oikkani.integrationservice.infrastructure.adapter.out.client.dto.request.AuctionDTO
import org.springframework.stereotype.Service


@Service
class AuctionGemService(
    private val apiClient: AuctionClient,
    private val exceptionNotification: ExceptionNotification
) : AuctionGemUseCase {

    private val logger = KotlinLogging.logger {}

    val gems = listOf(
        AuctionDTO(itemCode = 65021100, pairItemCode = 65022100, itemName = "10레벨 멸화의 보석"),
        AuctionDTO(itemCode = 65022100, pairItemCode = 65021100, itemName = "10레벨 홍염의 보석"),
        AuctionDTO(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석"),
        AuctionDTO(itemCode = 65032080, pairItemCode = 65031080, itemName = "8레벨 작열의 보석"),
        AuctionDTO(itemCode = 65031100, pairItemCode = 65032100, itemName = "10레벨 겁화의 보석"),
        AuctionDTO(itemCode = 65032100, pairItemCode = 65031100, itemName = "10레벨 작열의 보석")
    )

    override suspend fun fetchPrice() {
        TODO("Not yet implemented")
    }

    suspend fun fetchPriceAndReciveProcessorModule() {

        gems.forEach { gem ->
            try {
                val response = apiClient.fetchAuctionItemsAsync(gem.toGemRequest())
                // 성공 시 response 처리 로직 추가
                logger.info { "Successfully fetched price for ${gem.itemName}" }
            } catch (ex: Exception) {

            }
        }
    }

}