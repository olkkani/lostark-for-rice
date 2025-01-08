package io.olkkani.lfr.util

import io.olkkani.lfr.service.AuctionGemService
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component

@Component
class GemPricesRetrievalJob(
    private val service: AuctionGemService
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        service.fetchGemPrices()
    }
}

@Component
class SaveTodayPricesJob(
    private val service: AuctionGemService
) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
       service.saveTodayPrices()
    }
}

@Component
class ClearTodayPriceRecordJob (
    private val service: AuctionGemService
): QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) {
        service.clearTodayPricesRecord()
        service.fetchGemsPricesSync()
    }
}