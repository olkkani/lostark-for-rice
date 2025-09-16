package io.oikkani.processorservice.infrastructure.outbound.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.oikkani.processorservice.infrastructure.config.repository.RedisTestContainersConfig
import io.oikkani.processorservice.infrastructure.outbound.repository.entity.RefreshToken
import io.oikkani.processorservice.infrastructure.outbound.repository.redis.TokenRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.math.abs

//@SpringBootTest
@DataRedisTest
@ActiveProfiles("test")
@Import(RedisTestContainersConfig::class)
class TokenRepoTest: DescribeSpec(){

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Autowired
    private lateinit var tokenRepo: TokenRepo

    init {
        xdescribe("Token 저장 및 검증 테스트"){
            context("Redis Token Repo Test"){
                // Given
                val clientId = "testUser:device1"
                val refreshToken = "test-refresh-token-value"
                // When
                val token = RefreshToken(clientId, refreshToken)
                tokenRepo.save(token)
                it("생성한 토큰이 존재"){
                    // Then
                    val exists = tokenRepo.existTokenByClientIdAndToken(clientId, refreshToken)
                    exists shouldBe true
                }
                it("userAndDeviceID 만 동일한 값은 존재하지 않음"){
                    val notExistUserIdAndDeviceId = "testUser:device2"
                    val exists = tokenRepo.existTokenByClientIdAndToken(notExistUserIdAndDeviceId, refreshToken)
                    exists.shouldBeFalse()

                }
                it("refreshToken 만 동일한 값은 존재하지 않음"){
                    val notExistToken = "refresh-token-value-not-exist"
                    val exists = tokenRepo.existTokenByClientIdAndToken(clientId, notExistToken)
                    exists.shouldBeFalse()
                }
                it("TTL이 설정된 값과 거의 동일함") {
                    val isExpired = tokenRepo.existTokenByClientIdAndToken(clientId, refreshToken)
                    val redisKey = "refresh_token:$clientId"
                    val expireDate = redisTemplate.getExpire(redisKey)
                    val expectedTtl = 604800L

                    isExpired.shouldBeTrue()
                    // 설정 값과의 차이가 10초 이내면 정상
                    val difference = abs(expectedTtl - expireDate)
                    difference shouldBeLessThanOrEqual 10L
                }
            }
        }
    }
}