package io.olkkani.lfr.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
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
        // Redis는 기본적으로 6379 포트를 사용합니다
        private const val REDIS_PORT = 6379

        // GenericContainer를 사용하여 Redis 컨테이너 생성
        val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:8-alpine"))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true)

        init {
            redisContainer.start()

            // Redis 연결 정보를 환경 변수로 설정
            System.setProperty("spring.data.redis.host", redisContainer.host)
            System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(REDIS_PORT).toString())
        }
    }

    @Bean
    @ServiceConnection
    fun redisContainer(): GenericContainer<*> = redisContainer
}
