package com.zerobase.orderapi.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.zerobase.orderapi.repository"
        },
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderDataManagerConfig {

    private final DataSource orderDataSource;

    public OrderDataManagerConfig(
            @Qualifier("orderDataSource") DataSource orderDataSource) {
        this.orderDataSource = orderDataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        em.setDataSource(this.orderDataSource);
        em.setPersistenceUnitName("orderEntityManager");
        em.setPackagesToScan(
                "com.zerobase.orderapi.domain.order"
        );
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(orderJpaProperties());
        em.afterPropertiesSet();
        return em;
    }

    @Bean
    public JdbcTemplate orderJdbcTemplate(@Qualifier("orderDataSource") DataSource orderDataSource) {
        return new JdbcTemplate(orderDataSource);
    }

    private Properties orderJpaProperties() {
        Properties properties = new Properties();
//        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
        properties.setProperty(AvailableSettings.SHOW_SQL, "true");
        properties.setProperty(AvailableSettings.ALLOW_UPDATE_OUTSIDE_TRANSACTION, "true");
        properties.setProperty(AvailableSettings.PHYSICAL_NAMING_STRATEGY,"org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        return properties;
    }
}
