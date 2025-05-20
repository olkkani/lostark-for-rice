package io.olkkani.lfr.scheduler

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
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            itemPriceSnapshotScheduler.deleteSnapshotData()
            auctionScheduler.fetchPriceAndUpdatePrice()
            marketScheduler.fetchMaterialPriceAndUpdatePrice(isUpdateYesterdayAvgPrice = true)
            marketScheduler.fetchEngravingRecipePriceAndUpdatePrice(isUpdateYesterdayAvgPrice = true)
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