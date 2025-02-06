package io.olkkani.lfr.scheduler

import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class AuctionQuartzTrigger {
    @Bean
    fun todayOpeningJobDetail(): JobDetail {
        return JobBuilder.newJob(TodayOpeningJob::class.java)
            .withIdentity("TodayOpeningJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun todayFetchPricesJobDetail(): JobDetail {
        return JobBuilder.newJob(TodayFetchPricesJob::class.java)
            .withIdentity("TodayFetchPricesJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun todayClosingJobDetail(): JobDetail {
        return JobBuilder.newJob(TodayClosingJob::class.java)
            .withIdentity("TodayClosingJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun fetchGemPriceTriggerDailyStart0am(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-every-0am-to-1am, fetch-prices")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 15,30,45 0 ? * *"))
            .forJob(todayFetchPricesJobDetail())
            .build()
    @Bean
    fun fetchGemPriceTriggerDailyWithoutWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-every-fifteen-minutes-without-wednesday, fetch-prices")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */15 1-23 ? * MON,TUE,THU,FRI,SAT,SUN"))
            .forJob(todayFetchPricesJobDetail())
            .build()

    @Bean
    fun fetchPriceTriggerWed(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-wednesday-every-fifteen-minutes-without-maintenance-time, fetch-prices")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */15 1-5,10-23 ? * WED"))
            .forJob(todayFetchPricesJobDetail())
            .build()

    @Bean
    fun dayLastMinuteTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-day-last-one-minute")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 59 23 ? * *"))
            .forJob(todayClosingJobDetail())
            .build()

    @Bean
    fun midnightTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-midnight")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 ? * *"))
            .forJob(todayOpeningJobDetail())
            .build()
}