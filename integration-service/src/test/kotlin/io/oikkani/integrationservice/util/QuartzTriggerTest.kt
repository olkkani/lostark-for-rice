package io.oikkani.integrationservice.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.olkkani.lfr.config.PostgresqlTestContainersConfig
import io.olkkani.lfr.config.TestSecurityConfig
import io.olkkani.lfr.service.LostarkMarketSchedulerTest
import org.quartz.CronTrigger
import org.quartz.Scheduler
import org.quartz.TriggerKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("prod") // QuartzTrigger 빈을 로드하기 위해 "prod" 프로필을 활성화합니다.
@Import(PostgresqlTestContainersConfig::class, LostarkMarketSchedulerTest.TestConfig::class, TestSecurityConfig::class) // DB와 Mock 빈 설정을 재사용합니다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class QuartzTriggerTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var scheduler: Scheduler

    init {
        xdescribe("QuartzTrigger 설정 테스트") {
            it("midnightTrigger가 자정에 실행되도록 올바르게 설정되어야 합니다.") {
                // given
                val triggerKey = TriggerKey("trigger-midnight")

                // when
                val midnightTrigger = scheduler.getTrigger(triggerKey)

                // then
                midnightTrigger.shouldNotBeNull()
                midnightTrigger.shouldBeInstanceOf<CronTrigger>()
                (midnightTrigger as CronTrigger).cronExpression shouldBe "1 0 0 * * ?"
            }

            it("모든 트리거는 고유한 ID로 스케줄러에 등록되어야 합니다.") {
                // given
                val expectedTriggerKeys = listOf(
                    TriggerKey("trigger-every-0am-to-1am, fetch-prices"),
                    TriggerKey("trigger-every-fifteen-minutes-without-wednesday, fetch-prices"),
                    TriggerKey("trigger-wednesday-every-fifteen-minutes-without-maintenance-time, fetch-prices"),
                    TriggerKey("trigger-wednesday-every-fifteen-minutes-after-open-server, fetch-prices"),
                    TriggerKey("trigger-day-last-one-minute"),
                    TriggerKey("trigger-midnight")
                )

                // when
                // "default" 그룹의 모든 트리거 키를 가져옵니다.
                val actualTriggerKeys = scheduler.getTriggerKeys(null)

                // then
                actualTriggerKeys shouldContainAll expectedTriggerKeys
            }
        }
    }
}
