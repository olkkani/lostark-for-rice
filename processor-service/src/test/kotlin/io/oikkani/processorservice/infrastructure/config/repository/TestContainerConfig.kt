package io.oikkani.processorservice.infrastructure.config.repository

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class PostgresqlTestContainersConfig {

    companion object {
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)

        init {
            postgresContainer.start()

            // 환경 변수로 설정하여 Flyway가 이 DB를 사용하도록 함
            System.setProperty("spring.datasource.url", postgresContainer.jdbcUrl)
            System.setProperty("spring.datasource.username", postgresContainer.username)
            System.setProperty("spring.datasource.password", postgresContainer.password)
            System.setProperty("spring.flyway.enabled", "true")
        }
    }

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> = postgresContainer
}

@TestConfiguration(proxyBeanMethods = false)
class RedisTestContainersConfig {

    companion object {
        val redisContainer: RedisContainer = RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)

        init {
            redisContainer.start()
        }
    }

    @Bean
    fun redisContainer(): RedisContainer = redisContainer
}


