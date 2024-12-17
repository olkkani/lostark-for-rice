package io.olkkani.lfr.util

import io.olkkani.lfr.service.AuctionService
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class GemPricesRetrievalJob(
    private val service: AuctionService
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        service.fetchGemsPrice()
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