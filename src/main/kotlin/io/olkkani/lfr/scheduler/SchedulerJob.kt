package io.olkkani.lfr.scheduler

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
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            auctionScheduler.clearOldPriceRecord()
            auctionScheduler.fetchPriceAndUpdatePrice()
            marketScheduler.clearOldPriceRecord()
            marketScheduler.fetchPriceAndUpdatePrice(isUpdateYesterdayAvgPrice = true)
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
            marketScheduler.fetchPriceAndUpdatePrice()
        }
    }
}