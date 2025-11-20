package io.oikkani.processorservice.infrastructure.config.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .headers { header ->
                header.xssProtection { xss ->
                    xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                }
                header.frameOptions { it.deny() }
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSourceLocal())
            }
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().permitAll()
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .build()
    }

    @Bean
    fun corsConfigurationSourceLocal(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:80, https://gemspi.kro.kr/, http://ngnix:80")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    @ConditionalOnProperty(name = ["spring.h2.console.enabled"], havingValue = "true")
    @Profile("local")
    fun configureH2ConsoleEnable(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity? ->
            web!!.ignoring()
                .requestMatchers(PathRequest.toH2Console())
        }
    }
}