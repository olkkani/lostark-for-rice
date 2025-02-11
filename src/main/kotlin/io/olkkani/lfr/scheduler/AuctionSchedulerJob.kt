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
            service.clearOldPriceRecord()
            service.fetchPriceAndInsertOpenPrice()
            service.calculateGapTodayItemPrice()
        }
    }
}

@Component
class TodayFetchPricesJob(
    private val service: AuctionSchedulerService,
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            service.fetchPriceAndUpdatePrice()
            service.calculateGapTodayItemPrice()
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
            service.calculateGapTodayItemPrice()
        }
    }
}