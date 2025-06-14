package io.olkkani.lfr.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod")
class QuartzTrigger {
    private val logger = KotlinLogging.logger {}
    
    @Bean
    fun todayOpeningJobDetail(): JobDetail {
        logger.info { "Configuring TodayOpeningJob for prod profile" }
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
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */15 1-5,11-23 ? * WED"))
            .forJob(todayFetchPricesJobDetail())
            .build()

    @Bean
    fun fetchPriceTriggerWedOpenSever(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-wednesday-every-fifteen-minutes-after-open-server, fetch-prices")
            .withSchedule(CronScheduleBuilder.cronSchedule("1 */15 10-11 ? * WED"))
            .forJob(todayFetchPricesJobDetail())
            .build()

    @Bean
    fun dayLastMinuteTrigger(): Trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("trigger-day-last-one-minute")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 59 23 ? * *"))
            .forJob(todayFetchPricesJobDetail())
            .build()

    @Bean
    fun midnightTrigger(): Trigger {
        logger.info { "Configuring midnight trigger for TodayOpeningJob - will run at 00:00:01 daily" }
        return TriggerBuilder.newTrigger()
            .withIdentity("trigger-midnight")
            .withSchedule(CronScheduleBuilder.cronSchedule("1 0 0 * * ?"))
            .forJob(todayOpeningJobDetail())
            .build()
    }

    // 임시 테스트용 - 매 5분마다 실행 (테스트 후 제거 필요)
    @Bean 
    fun testTrigger(): Trigger {
        logger.info { "Configuring TEST trigger for TodayOpeningJob - will run every 5 minutes" }
        return TriggerBuilder.newTrigger()
            .withIdentity("trigger-test-5min")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * ? * *"))
            .forJob(todayOpeningJobDetail())
            .build()
    }

}