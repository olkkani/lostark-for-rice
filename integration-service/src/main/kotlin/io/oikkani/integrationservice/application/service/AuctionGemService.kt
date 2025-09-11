package io.oikkani.integrationservice.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.inbound.AuctionGemUseCase
import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.AuctionClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.lostark.dto.request.AuctionDTO
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.ProcessorAuctionClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.dto.AuctionPriceRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service


@Service
class AuctionGemService(
    private val apiClient: AuctionClient,
    private val processorClient: ProcessorAuctionClient,
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
    override suspend fun fetchAndSendPriceData() = coroutineScope {
        gems.map { gem ->
            async {
                val response = apiClient.fetchItemsAsync(gem.toGemRequest())
                response?.let { data ->
                    launch {
                        processorClient.sendAuctionPriceData(
                            AuctionPriceRequest(
                                itemCode = gem.itemCode,
                                prices = data.toDomain(),
                                //TODO isOpening job 이 필요한지 확인
                            )
                        )
                    }
                }
            }
        }.awaitAll().let {  }
    }
}

