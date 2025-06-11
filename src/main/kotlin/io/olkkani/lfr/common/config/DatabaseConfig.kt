package io.olkkani.lfr.common.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import redis.embedded.RedisServer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@Configuration
@EnableRedisRepositories
class RedisConfiguration(
    @Value("\${redis.host:localhost}") private val host: String,
    @Value("\${redis.port:6379}") private val redisPort: Int,
    @Value("\${redis.password:}") private val redisPassword: String,
) {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration().apply {
            hostName = host
            port = redisPort
            if (redisPassword.isNotEmpty()) {
                setPassword(redisPassword)
            }
        }
        return LettuceConnectionFactory(config)
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: LettuceConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = redisConnectionFactory
        return template
    }

}

@Profile("local")
@Configuration
public class RedisEmbeddedConfig(
    @Value("\${redis.port:0}") private var redisPort: Int
) {
    private val logger = KotlinLogging.logger {  }
    private lateinit var redisServer: RedisServer

    @PostConstruct
    @Throws(IOException::class)
    private fun start() {
        val port = if (isRedisRunning()) findAvailablePort() else redisPort
        redisServer = RedisServer(port)
        redisServer.start()
    }

    @PreDestroy
    @Throws(IOException::class)
    private fun stop() {
        redisServer.stop()
    }

    @Throws(IOException::class)
    private fun isRedisRunning(): Boolean {
        return isRunning(executeGrepProcessCommand(redisPort))
    }

    @Throws(IOException::class)
    fun findAvailablePort(): Int {
        for (port in 10000..65535) {
            val process = executeGrepProcessCommand(port)
            if (!isRunning(process)) {
                return port
            }
        }
        throw IllegalArgumentException("Not Found Available port: 10000 ~ 65535")
    }

    @Throws(IOException::class)
    private fun executeGrepProcessCommand(port: Int): Process {
        val command = String.format("netstat -nat | grep LISTEN|grep %d", port)
        val shell = arrayOf<String?>("/bin/sh", "-c", command)
        return Runtime.getRuntime().exec(shell)
    }

    private fun isRunning(process: Process): Boolean {
        var line: String?
        val pidInfo = StringBuilder()
        try {
            BufferedReader(InputStreamReader(process.inputStream)).use { input ->
                while ((input.readLine().also { line = it }) != null) {
                    pidInfo.append(line)
                }
            }
        } catch (e: Exception) {
            logger.error{e.message}
        }
        return pidInfo.isNotEmpty()
    }
}