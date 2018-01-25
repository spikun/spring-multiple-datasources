import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import liquibase.integration.spring.SpringLiquibase;

public abstract class AbstractDbConfig {

    protected final JpaProperties properties;

    public AbstractDbConfig(final JpaProperties jpaProperties) {
        this.properties = jpaProperties;
    }

    public abstract DataSource dataSource();

    public abstract DataSourceProperties dataSourceProperties();

    public abstract PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory);

    public abstract LocalContainerEntityManagerFactoryBean entityManager(final DataSource dataSource);

    public abstract LiquibaseProperties liquibaseProperties();

    public abstract SpringLiquibase liquibase(final DataSource dataSource, final LiquibaseProperties properties);

    protected LocalContainerEntityManagerFactoryBean createEntityManagerBean(
            final String packagesToScan,
            final DataSource dataSource) {
        final Map<String, Object> vendorProperties = getVendorProperties(dataSource);

        final LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setPackagesToScan(packagesToScan);
        entityManager.setDataSource(dataSource);
        entityManager.setJpaVendorAdapter(createJpaVendorAdapter(dataSource));
        entityManager.setJpaPropertyMap(vendorProperties);

        return entityManager;
    }

    protected SpringLiquibase createSpringLiquibase(final DataSource dataSource, final LiquibaseProperties properties) {
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabels(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        return liquibase;
    }

    protected AbstractJpaVendorAdapter createJpaVendorAdapter(final DataSource dataSource) {
        final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(this.properties.isShowSql());
        adapter.setDatabase(this.properties.determineDatabase(dataSource));
        adapter.setDatabasePlatform(this.properties.getDatabasePlatform());
        adapter.setGenerateDdl(this.properties.isGenerateDdl());
        return adapter;
    }

    protected Map<String, Object> getVendorProperties(final DataSource dataSource) {
        final Map<String, Object> vendorProperties = new LinkedHashMap<String, Object>();
        vendorProperties.putAll(this.properties.getHibernateProperties(dataSource));
        return vendorProperties;
    }
}