
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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
        basePackages = { "ee.example.core.primary.repository" },
        transactionManagerRef = "primaryTransactionManager",
        entityManagerFactoryRef = "primaryEntityManager")
public class RawDbConfig extends AbstractDbConfig {

    public RawDbConfig(final JpaProperties properties) {
        super(properties);
    }

    @Primary
    @Bean("primaryDataSource")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean("primaryDataSourceProperties")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("primaryTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("primaryEntityManager") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean("primaryEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManager(@Qualifier("primaryDataSource") final DataSource dataSource) {
        return createEntityManagerBean("ee.example.core.primary.entity", dataSource);
    }

    @Bean(name = "primaryLiquibaseProperties")
    @ConfigurationProperties("liquibase-changelogs.primary.liquibase")
    public LiquibaseProperties liquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean("liquibase")
    public SpringLiquibase liquibase(
            @Qualifier("primaryDataSource") final DataSource dataSource,
            @Qualifier("primaryLiquibaseProperties") final LiquibaseProperties properties) {

        return createSpringLiquibase(dataSource, properties);
    }
}
