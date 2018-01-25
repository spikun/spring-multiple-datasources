
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
        basePackages = { "ee.example.core.secondary.repository" },
        transactionManagerRef = "secondaryTransactionManager",
        entityManagerFactoryRef = "secondaryEntityManager")
public class SecondaryDbConfig extends AbstractDbConfig {

    public SecondaryDbConfig(final JpaProperties properties) {
        super(properties);
    }

    @Bean("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean("secondaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("secondaryTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("secondaryEntityManager") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean("secondaryEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(@Qualifier("secondaryDataSource") final DataSource dataSource) {
        return createEntityManagerBean("ee.example.core.secondary.entity", dataSource);
    }

    @Bean(name = "secondaryLiquibaseProperties")
    @ConfigurationProperties("liquibase-changelogs.secondary.liquibase")
    public LiquibaseProperties liquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean("secondaryLiquibase")
    public SpringLiquibase liquibase(
            @Qualifier("secondaryDataSource") final DataSource dataSource,
            @Qualifier("secondaryLiquibaseProperties") final LiquibaseProperties properties) {

        return createSpringLiquibase(dataSource, properties);
    }

}
