package io.olkkani.lfr.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["io.olkkani.lfr.repository.jpa"],
    entityManagerFactoryRef = "jpaEntityManagerFactory",
    transactionManagerRef = "jpaTransactionManager"
)
class JpaConfig {

    @Primary
    @Bean(name = ["jpaEntityManagerFactory"])
    fun jpaEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("io.olkkani.lfr.entity.jpa")
            .persistenceUnit("jpa")
            .build()
    }

    @Primary
    @Bean(name = ["jpaTransactionManager"])
    fun jpaTransactionManager(
        @Qualifier("jpaEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}

@Configuration
@EnableMongoRepositories(
    basePackages = ["io.olkkani.lfr.repository.mongo"],
    mongoTemplateRef = "mongoTemplate"
)
class MongoConfig {

    @Bean(name = ["mongoTransactionManager"])
    fun mongoTransactionManager(
        mongoDatabaseFactory: MongoDatabaseFactory
    ): PlatformTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory)
    }
}