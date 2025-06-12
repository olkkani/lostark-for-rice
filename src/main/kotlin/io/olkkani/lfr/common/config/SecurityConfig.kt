package io.olkkani.lfr.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.olkkani.lfr.common.security.JwtAuthenticationFilter
import io.olkkani.lfr.common.security.JwtTokenProvider
import io.olkkani.lfr.common.security.OAuth2LoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
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
    private val oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler,
    private val oAuth2LoginFailureHandler: OAuth2LoginFailureHandler,
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
}