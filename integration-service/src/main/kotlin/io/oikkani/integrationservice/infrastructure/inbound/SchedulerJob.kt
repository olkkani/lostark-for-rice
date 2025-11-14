package io.oikkani.integrationservice.infrastructure.inbound

import io.oikkani.integrationservice.application.port.inbound.AuctionFetchUseCase
import io.oikkani.integrationservice.application.port.inbound.MarketFetchMaterialUseCase
import io.oikkani.integrationservice.application.port.inbound.MarketFetchRecipeUseCase
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorAuctionClient
import io.oikkani.integrationservice.infrastructure.outbound.client.processor.ProcessorMarketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class TodayOpeningJob(
    private val auctionGemService: AuctionFetchUseCase,
    private val marketMaterialService: MarketFetchMaterialUseCase,
    private val marketRecipeService: MarketFetchRecipeUseCase,
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            auctionGemService.fetchAndSendPriceData()
            marketMaterialService.fetchAndSendPriceData(isUpdateYesterdayAvgPrice = true)
            marketRecipeService.fetchAndSendPriceData(isUpdateYesterdayAvgPrice = true)
        }
    }
}

@Component
class TodayFetchPricesJob(
    private val auctionGemService: AuctionFetchUseCase,
    private val marketMaterialService: MarketFetchMaterialUseCase,
    private val marketRecipeService: MarketFetchRecipeUseCase,
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            auctionGemService.fetchAndSendPriceData()
            marketMaterialService.fetchAndSendPriceData()
            marketRecipeService.fetchAndSendPriceData()
        }
    }
}


@Component
class TodayLastJob(
    private val processorAuctionClient: ProcessorAuctionClient,
    private val processorMarketClient: ProcessorMarketClient,
) : QuartzJobBean() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun executeInternal(context: JobExecutionContext) {
        coroutineScope.launch {
            processorAuctionClient.deleteTodayPricesSnapshot()
            processorMarketClient.deleteTodayPricesSnapshot()
        }
    }

}