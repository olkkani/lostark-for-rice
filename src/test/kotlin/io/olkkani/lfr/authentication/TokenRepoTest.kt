package io.olkkani.lfr.authentication

import io.kotest.core.spec.style.DescribeSpec
import io.olkkani.lfr.config.RedisTestContainersConfig
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataRedisTest
@ActiveProfiles("test")
@Import(RedisTestContainersConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
class TokenRepoTest: DescribeSpec(){

    init {
        describe("Token Repository Test"){
            context("Redis Token Repo Test"){
                it(""){

                }
            }
        }
    }
}