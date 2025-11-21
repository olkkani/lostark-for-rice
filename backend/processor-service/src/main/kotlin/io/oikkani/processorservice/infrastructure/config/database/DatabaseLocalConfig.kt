package io.oikkani.processorservice.infrastructure.config.database

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Profile("local")
@Configuration
class DatabaseLocalConfig {
    @Bean
    fun dslContext(dataSource: DataSource): DSLContext {
        val configuration = DefaultConfiguration()
        configuration.set(dataSource)
        configuration.set(SQLDialect.POSTGRES) // PostgreSQL 방언 명시

        return DSL.using(configuration)
    }
}