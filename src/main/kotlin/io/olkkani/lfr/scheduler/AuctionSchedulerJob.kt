package io.olkkani.lfr.scheduler

import io.olkkani.lfr.service.AuctionSchedulerService
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class TodayOpeningJob(
    private val service: AuctionSchedulerService,
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            service.clearTodayPriceRecord()
            service.fetchPriceAndInsertOpenPrice()
//            serviceOld.fetchGemPrices()
//            serviceOld.updatePairItemPriceGap()
        }
    }
}

@Component
class TodayFetchPricesJob(
//    private val service: AuctionGemService
    private val service: AuctionSchedulerService,
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
//        service.saveTodayPrices()
        runBlocking {
            service.fetchPriceAndUpdateLowAndHighPrice()
        }
    }
}

@Component
class TodayClosingJob(
    private val service: AuctionSchedulerService
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            service.fetchPriceAndUpdateClosePrice()

//            service.clearTodayPricesRecord()
//            service.updatePrevPriceTrend()
//            service.fetchGemPrices()
//            service.updatePairItemPriceGap()
        }
    }
}