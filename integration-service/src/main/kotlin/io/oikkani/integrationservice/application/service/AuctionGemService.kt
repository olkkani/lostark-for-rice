package io.oikkani.integrationservice.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.inbound.AuctionGemUseCase
import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AuctionItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.AuctionClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorAuctionClient
import io.olkkani.common.dto.contract.AuctionPriceSnapshot
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
        AuctionItemCondition(itemCode = 65021100, pairItemCode = 65022100, itemName = "10레벨 멸화의 보석"),
        AuctionItemCondition(itemCode = 65022100, pairItemCode = 65021100, itemName = "10레벨 홍염의 보석"),
        AuctionItemCondition(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석"),
        AuctionItemCondition(itemCode = 65032080, pairItemCode = 65031080, itemName = "8레벨 작열의 보석"),
        AuctionItemCondition(itemCode = 65031100, pairItemCode = 65032100, itemName = "10레벨 겁화의 보석"),
        AuctionItemCondition(itemCode = 65032100, pairItemCode = 65031100, itemName = "10레벨 작열의 보석")
    )
    override suspend fun fetchAndSendPriceData(isUpdateOpenPrice: Boolean) = coroutineScope {
        gems.map { gem ->
            async {
                val response = apiClient.fetchItemsAsync(gem.toGemRequest())
                response?.let { data ->
                    launch {
                        processorClient.sendAuctionPriceData(
                            AuctionPriceSnapshot(
                                itemCode = gem.itemCode,
                                prices = data.toDomain(),
                                isUpdateOpenPrice = isUpdateOpenPrice
                            )
                        )
                    }
                }
            }
        }.awaitAll().let {  }
    }
}

