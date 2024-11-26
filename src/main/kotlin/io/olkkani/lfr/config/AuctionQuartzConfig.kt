package io.olkkani.lfr.config

import io.olkkani.lfr.scheduler.GemOpenPricesRetrievalJob
import io.olkkani.lfr.scheduler.GemPricesRetrievalJob
import io.olkkani.lfr.scheduler.SaveTodayPricesJob
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class AuctionQuartzConfig {

    @Bean
    fun scheduler(): Scheduler {
        val scheduler = StdSchedulerFactory().scheduler
        scheduler.start()
        return scheduler
    }

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
    fun gemPriceRetrievalTriggerDaily0010(): List<Trigger> {
        val cronExpression1 = "0 10 0 ? * MON,TUE,THU,FRI,SAT,SUN"
        val cronExpression2 = "0 10 10 ? * WED"

        val dailyExceptWedTrigger = TriggerBuilder.newTrigger()
            .forJob(gemOpenPricesRetrievalJobDetail())
            .withIdentity("trigger-daily0010")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression1))
            .build()

        val wednesdayTrigger = TriggerBuilder.newTrigger()
            .forJob(gemOpenPricesRetrievalJobDetail())
            .withIdentity("trigger-web1010")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression2))
            .build()

        scheduler().scheduleJob(dailyExceptWedTrigger)
        scheduler().scheduleJob(wednesdayTrigger)

        return listOf(dailyExceptWedTrigger, wednesdayTrigger)
    }

    @Bean
    fun gemPriceRetrievalTriggerEvery2HoursWedExcluding6to10(): List<Trigger> {
        val cronExpression1 = "0 0 1-23/2 ? * MON,TUE,THU,FRI,SAT,SUN"
        val cronExpression2 = "0 0 1-5,11-23/2 ? * WED"

        val everyOddHourExceptWednesdayTrigger = TriggerBuilder.newTrigger()
            .forJob(gemPricesRetrievalJobDetail())
            .withIdentity("trigger-everyOddHourExceptWed")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression1))
            .build()

        val wednesdayBiHourlyNightTrigger = TriggerBuilder.newTrigger()
            .forJob(gemPricesRetrievalJobDetail())
            .withIdentity("trigger-WednesdayBiHourlyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression2))
            .build()

        scheduler().scheduleJob(everyOddHourExceptWednesdayTrigger)
        scheduler().scheduleJob(wednesdayBiHourlyNightTrigger)

        return listOf(everyOddHourExceptWednesdayTrigger, wednesdayBiHourlyNightTrigger)
    }

    @Bean
    fun saveGemPriceTriggerDaily2350(): Trigger {
        val cronExpression = "0 50 23 ? * *"

        val everyNightTrigger = TriggerBuilder.newTrigger()
            .forJob(saveTodayPricesJobDetail())
            .withIdentity("trigger-everyNight")
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
            .build()

        scheduler().scheduleJob(everyNightTrigger)

        return everyNightTrigger
    }
}
