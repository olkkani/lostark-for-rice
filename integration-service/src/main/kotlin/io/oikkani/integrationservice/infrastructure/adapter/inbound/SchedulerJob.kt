package io.oikkani.integrationservice.infrastructure.adapter.inbound

import io.github.oshai.kotlinlogging.KotlinLogging
import io.oikkani.integrationservice.application.port.inbound.AuctionGemUseCase
import io.oikkani.integrationservice.application.port.inbound.MarketMaterialUseCase
import io.oikkani.integrationservice.application.port.inbound.MarketRecipeUseCase
import io.oikkani.integrationservice.application.service.AuctionGemService
import io.oikkani.integrationservice.application.service.MarketMaterialService
import io.oikkani.integrationservice.application.service.MarketRecipeService
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.ProcessorAuctionClient
import io.oikkani.integrationservice.infrastructure.adapter.outbound.client.processor.ProcessorMarketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class TodayOpeningJob(
    private val auctionGemService: AuctionGemUseCase,
    private val marketMaterialService: MarketMaterialUseCase,
    private val marketRecipeService: MarketRecipeUseCase,
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            auctionGemService.fetchAndSendPriceData(isUpdateOpenPrice = true)
            marketMaterialService.fetchAndSendPriceData(isUpdateOpenPriceAndYesterdayAvgPrice = true)
            marketRecipeService.fetchAndSendPriceDate(isUpdateOpenPriceAndYesterdayAvgPrice = true)
        }
    }
}

@Component
class TodayFetchPricesJob(
    private val auctionGemService: AuctionGemUseCase,
    private val marketMaterialService: MarketMaterialUseCase,
    private val marketRecipeService: MarketRecipeUseCase,
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            auctionGemService.fetchAndSendPriceData(isUpdateOpenPrice = false)
            marketMaterialService.fetchAndSendPriceData(isUpdateOpenPriceAndYesterdayAvgPrice = false)
            marketRecipeService.fetchAndSendPriceDate(isUpdateOpenPriceAndYesterdayAvgPrice = false)
        }
    }
}


@Component
class TodayLastJob(
    private val processorAuctionClient: ProcessorAuctionClient,
    private val processorMarketClient: ProcessorMarketClient
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            processorAuctionClient.deleteTodayPricesSnapshot()
            processorMarketClient.deleteTodayPricesSnapshot()
        }
    }

}