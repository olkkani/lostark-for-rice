package io.olkkani.lfr.config

import io.olkkani.lfr.util.GemOpenPricesRetrievalJob
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
    fun gemOpenPricesRetrievalJobDetail(): JobDetail {
        return JobBuilder.newJob(GemOpenPricesRetrievalJob::class.java)
            .withIdentity("gemOpenPricesRetrievalJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun gemPricesRetrievalJobDetail(): JobDetail {
        return JobBuilder.newJob(GemPricesRetrievalJob::class.java)
            .withIdentity("gemPricesRetrievalJob")
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
    fun fetchGemOpenPriceTriggerDailyWithoutWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-fetchGemPriceTriggerDaily0010WithWed")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 10 0 ? * MON,TUE,THU,FRI,SAT,SUN"))
            .forJob(gemOpenPricesRetrievalJobDetail())
            .build()
    @Bean
    fun fetchGemOpenPriceTriggerWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-fetchGemPriceTriggerWed1010")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 10 10 ? * WED"))
            .forJob(gemOpenPricesRetrievalJobDetail())
            .build()

    @Bean
    fun fetchGemPriceTriggerDailyWithoutWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-everyOddHourExceptWed")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1-23/2 ? * MON,TUE,THU,FRI,SAT,SUN"))
            .forJob(gemPricesRetrievalJobDetail())
            .build()
    @Bean
    fun fetchGemPriceTriggerWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-WednesdayBiHourlyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1-5,11-23/2 ? * WED"))
            .forJob(gemPricesRetrievalJobDetail())
            .build()

    @Bean
    fun saveGemPriceTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-everyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 50 23 ? * *"))
            .forJob(saveTodayPricesJobDetail())
            .build()
}
