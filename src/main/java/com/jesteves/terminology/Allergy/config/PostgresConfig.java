package com.jesteves.terminology.Allergy.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.jesteves.terminology.Allergy.log.repository",
        entityManagerFactoryRef = "pgEntityManagerFactory",
        transactionManagerRef = "pgTransactionManager"
)
public class PostgresConfig {

    @Value("${app.jpa.pg.hibernate.ddl-auto:none}")
    private String pgDdlAuto;
    @Value("${app.jpa.pg.show-sql:false}")
    private String pgShowSql;
    @Value("${app.jpa.pg.properties.hibernate.format_sql:false}")
    private String pgFormatSql;
    @Value("${app.jpa.pg.properties.hibernate.dialect:org.hibernate.dialect.PostgreSQLDialect}")
    private String pgDialect;


    @Bean("pgDataSourceProperties")
    @ConfigurationProperties("app.datasource.pg")
    public DataSourceProperties pgDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean("pgDataSource")
    public DataSource pgDataSource(@Qualifier("pgDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean("pgEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pgEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pgDataSource") DataSource dataSource) {

        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", pgDdlAuto);
        jpaProperties.put("hibernate.dialect", pgDialect);
        jpaProperties.put("hibernate.show_sql", pgShowSql);
        jpaProperties.put("hibernate.format_sql", pgFormatSql);

        return builder
                .dataSource(dataSource)
                .packages("com.jesteves.terminology.Allergy.log.entity")
                .persistenceUnit("postgres")
                .properties(jpaProperties)
                .build();
    }

    @Bean("pgTransactionManager")
    public PlatformTransactionManager pgTransactionManager(
            @Qualifier("pgEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}