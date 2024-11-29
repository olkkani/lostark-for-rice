package io.olkkani.lfr.scheduler

import io.olkkani.lfr.service.AuctionService
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class GemOpenPricesRetrievalJob(
    private val service: AuctionService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        service.getGemOpenPrices()
    }
}

@Component
class GemPricesRetrievalJob(
    private val service: AuctionService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        service.fetchingGemsPrices()
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