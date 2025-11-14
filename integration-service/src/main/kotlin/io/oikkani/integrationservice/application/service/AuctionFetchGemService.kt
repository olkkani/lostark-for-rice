package io.oikkani.integrationservice.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.inbound.AuctionFetchUseCase
import io.oikkani.integrationservice.application.port.outbound.ExceptionNotification
import io.oikkani.integrationservice.domain.dto.AuctionItemCondition
import io.oikkani.integrationservice.infrastructure.outbound.client.lostark.AuctionClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorAuctionClient
import io.olkkani.common.dto.contract.AuctionItemPrice
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service


@Service
class AuctionFetchGemService(
    private val apiClient: AuctionClient,
    private val processorClient: ProcessorAuctionClient,
    private val exceptionNotification: ExceptionNotification
) : AuctionFetchUseCase {

    private val logger = KotlinLogging.logger {}

    val gems = listOf(
        AuctionItemCondition(itemCode = 65021100, pairItemCode = 65022100, itemName = "10레벨 멸화의 보석"),
        AuctionItemCondition(itemCode = 65022100, pairItemCode = 65021100, itemName = "10레벨 홍염의 보석"),
        AuctionItemCondition(itemCode = 65031080, pairItemCode = 65032080, itemName = "8레벨 겁화의 보석"),
        AuctionItemCondition(itemCode = 65032080, pairItemCode = 65031080, itemName = "8레벨 작열의 보석"),
        AuctionItemCondition(itemCode = 65031100, pairItemCode = 65032100, itemName = "10레벨 겁화의 보석"),
        AuctionItemCondition(itemCode = 65032100, pairItemCode = 65031100, itemName = "10레벨 작열의 보석")
    )
    override suspend fun fetchAndSendPriceData() = coroutineScope {
        gems.map { gem ->
            async {
                val response = apiClient.fetchItems(gem.toGemRequest())
                response?.let { data ->
                    processorClient.sendAuctionPriceData(
                        AuctionItemPrice(
                            itemCode = gem.itemCode,
                            prices = data.toAuctionPrices(),
                        )
                    )
                }
            }
        }.awaitAll().let {  }
    }
}

