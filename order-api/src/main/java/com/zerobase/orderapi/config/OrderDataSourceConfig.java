package com.zerobase.orderapi.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class OrderDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.order-datasource")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory orderEntityManagerFactory) {
        JpaTransactionManager orderTransactionManager = new JpaTransactionManager();
        orderTransactionManager.setEntityManagerFactory(orderEntityManagerFactory);
        return orderTransactionManager;
    }
}
