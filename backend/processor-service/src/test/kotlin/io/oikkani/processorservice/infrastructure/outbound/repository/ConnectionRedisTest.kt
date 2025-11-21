package io.oikkani.processorservice.infrastructure.outbound.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.infrastructure.config.repository.RedisTestContainersConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate

@SpringBootTest
@Import(RedisTestContainersConfig::class)
class ConnectionRedisTest : DescribeSpec() {

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    init {

        xdescribe("Redis 연동 테스트") {
            context("데이터 저장 및 조회") {
                it("Redis에 데이터를 저장하고 조회할 수 있다") {
                    // Given
                    val key = "test:key"
                    val value = "test:value"

                    // When
                    redisTemplate.opsForValue().set(key, value)
                    val result = redisTemplate.opsForValue().get(key)

                    // Then
                    result shouldBe value
                }

                it("존재하지 않는 키 조회시 null을 반환한다") {
                    // Given
                    val nonExistentKey = "test:nonexistent"

                    // When
                    val result = redisTemplate.opsForValue().get(nonExistentKey)

                    // Then
                    result shouldBe null
                }
            }
        }
    }
}