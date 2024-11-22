package io.oikkani.lfr.scheduler

import io.oikkani.lfr.service.AuctionService
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class GemOpenPricesRetrievalJob(
    private val service: AuctionService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        service.getGemOpenPricesScheduler()
    }
}

@Component
class GemPricesRetrievalJob(
    private val service: AuctionService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        service.getGemPricesScheduler()
    }
}

@Component
class SaveTodayPricesJob(
    private val service: AuctionService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        service.saveTodayGemsPrices()
    }
}