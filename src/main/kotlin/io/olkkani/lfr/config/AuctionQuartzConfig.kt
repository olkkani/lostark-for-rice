package io.olkkani.lfr.config

import io.olkkani.lfr.util.ClearTodayPriceRecordJob
import io.olkkani.lfr.util.GemPricesRetrievalJob
import io.olkkani.lfr.util.SaveTodayPricesJob
import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class AuctionQuartzConfig {
    @Bean
    fun gemPricesRetrievalJobDetail(): JobDetail {
        return JobBuilder.newJob(GemPricesRetrievalJob::class.java)
            .withIdentity("gemOpenPricesRetrievalJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun saveTodayPricesJobDetail(): JobDetail {
        return JobBuilder.newJob(SaveTodayPricesJob::class.java)
            .withIdentity("saveTodayPricesJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun clearTodayPriceRecordJobDetail(): JobDetail {
        return JobBuilder.newJob(ClearTodayPriceRecordJob::class.java)
            .withIdentity("clearTodayPriceRecordJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun fetchGemPriceTriggerDailyWithoutWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-everyOddHourExceptWed")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */15 0-23 ? * MON,TUE,THU,FRI,SAT,SUN"))
            .forJob(gemPricesRetrievalJobDetail())
            .build()
    @Bean
    fun fetchGemPriceTriggerWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-WednesdayBiHourlyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */15 0-5,10-23 ? * WED"))
            .forJob(gemPricesRetrievalJobDetail())
            .build()

    @Bean
    fun saveGemPriceTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-everyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 50 23 ? * *"))
            .forJob(saveTodayPricesJobDetail())
            .build()

    @Bean
    fun clearTodayPriceRecordTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-everyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *"))
            .forJob(clearTodayPriceRecordJobDetail())
            .build()
}
