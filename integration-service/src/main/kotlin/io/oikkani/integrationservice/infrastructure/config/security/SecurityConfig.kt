package io.oikkani.integrationservice.infrastructure.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.oikkani.integrationservice.infrastructure.adapter.inbound.security.JwtAuthenticationFilter
import io.oikkani.integrationservice.infrastructure.adapter.inbound.security.JwtTokenProvider
import io.oikkani.integrationservice.infrastructure.adapter.inbound.security.OAuth2AuthenticationFailureHandler
import io.oikkani.integrationservice.infrastructure.adapter.inbound.security.OAuth2AuthenticationSuccessHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val oAuth2LoginSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2LoginFailureHandler: OAuth2AuthenticationFailureHandler,
) {
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
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/api/alerts/**").authenticated()
                authorize.anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .successHandler(oAuth2LoginSuccessHandler)
                    .failureHandler(oAuth2LoginFailureHandler)
                    .authorizationEndpoint { authEndpoint ->
                        authEndpoint.baseUri("/oauth2/authorization")
                    }
                    .redirectionEndpoint { redirectEndpoint ->
                        redirectEndpoint.baseUri("/login/oauth2/code/*")
                    }
            }
            .build()
    }

    @Bean
    fun corsConfigurationSourceLocal(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173, https://gemspi.kro.kr/")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jsonEscapeConverter(): MappingJackson2HttpMessageConverter {
        val copy = objectMapper.copy()
        copy.factory.characterEscapes = HtmlCharacterEscapes()
        return MappingJackson2HttpMessageConverter(copy)
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