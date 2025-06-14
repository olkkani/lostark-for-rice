package io.olkkani.lfr.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import io.olkkani.lfr.service.ItemPriceSnapshotScheduler
import io.olkkani.lfr.service.LostarkAuctionScheduler
import io.olkkani.lfr.service.LostarkMarketScheduler
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class TodayOpeningJob(
    private val auctionScheduler: LostarkAuctionScheduler,
    private val marketScheduler: LostarkMarketScheduler,
    private val itemPriceSnapshotScheduler: ItemPriceSnapshotScheduler
) : QuartzJobBean() {
    private val logger = KotlinLogging.logger {}
    
    override fun executeInternal(context: JobExecutionContext) {
        try {
            runBlocking {
                itemPriceSnapshotScheduler.deleteSnapshotData()
                auctionScheduler.fetchPriceAndUpdatePrice()
                marketScheduler.fetchMaterialPriceAndUpdatePrice(isUpdateYesterdayAvgPrice = true)
                marketScheduler.fetchEngravingRecipePriceAndUpdatePrice(isUpdateYesterdayAvgPrice = true)
            }
        } catch (e: Exception) {
            logger.error(e) { "=== TodayOpeningJob failed with exception: ${e.message} ===" }
            throw e
        }
    }
}

@Component
class TodayFetchPricesJob(
    private val auctionScheduler: LostarkAuctionScheduler,
    private val marketScheduler: LostarkMarketScheduler,
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            auctionScheduler.fetchPriceAndUpdatePrice()
            marketScheduler.fetchMaterialPriceAndUpdatePrice()
            marketScheduler.fetchEngravingRecipePriceAndUpdatePrice()
        }
    }
}