package com.jesteves.terminology.Allergy.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.jesteves.terminology.Allergy.repository",
        entityManagerFactoryRef = "sqliteEntityManagerFactory",
        transactionManagerRef = "sqliteTransactionManager"
)
public class SqliteConfig {

    @Value("${app.jpa.sqlite.hibernate.ddl-auto:none}")
    private String sqliteDdlAuto;
    @Value("${app.jpa.sqlite.show-sql:false}")
    private String sqliteShowSql;
    @Value("${app.jpa.sqlite.properties.hibernate.format_sql:false}")
    private String sqliteFormatSql;
    @Value("${app.jpa.sqlite.properties.hibernate.dialect:org.hibernate.community.dialect.SQLiteDialect}")
    private String sqliteDialect;

    @Primary
    @Bean("sqliteDataSourceProperties")
    @ConfigurationProperties("app.datasource.sqlite")
    public DataSourceProperties sqliteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("sqliteDataSource")
    public DataSource sqliteDataSource(@Qualifier("sqliteDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean("sqliteEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sqliteEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sqliteDataSource") DataSource dataSource) {

        Map<String, String> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", sqliteDdlAuto);
        jpaProperties.put("hibernate.dialect", sqliteDialect);
        jpaProperties.put("hibernate.show_sql", sqliteShowSql);
        jpaProperties.put("hibernate.format_sql", sqliteFormatSql);

        return builder
                .dataSource(dataSource)
                .packages("com.jesteves.terminology.Allergy.entity")
                .persistenceUnit("sqlite")
                .properties(jpaProperties)
                .build();
    }

    @Primary
    @Bean("sqliteTransactionManager")
    public PlatformTransactionManager sqliteTransactionManager(
            @Qualifier("sqliteEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}