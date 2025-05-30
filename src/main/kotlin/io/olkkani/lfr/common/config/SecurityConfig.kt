package io.olkkani.lfr.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers { header ->
                header.xssProtection { xss ->
                    xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                }
                header.frameOptions { it.deny() }
//                header.contentSecurityPolicy { it.policyDirectives("script-src 'self'") }
//                header.httpStrictTransportSecurity {
//                    it.includeSubDomains(true)
//                    it.maxAgeInSeconds(31536000)
//                }
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorize ->
//                authorize.requestMatchers("/api/alerts/**").authenticated()
                authorize.anyRequest().permitAll()
            }
//            .oauth2Login { succeeHandler ->
//                succeeHandler.userInfoEndpoint {
//                    it.oidcUserService(OidcUserService())
//                }
//                succeeHandler.defaultSuccessUrl("/api/alerts/fetch", true)

//            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSourceLocal())
            }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun corsConfigurationSourceLocal(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:5173, https://gemspi.kro.kr/")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
//            maxAge = 3600L
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