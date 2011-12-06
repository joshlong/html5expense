package com.springsource.html5expense.config;

import javax.sql.DataSource;

import org.h2.Driver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
@Profile("local")
public class LocalServicesConfig implements ServicesConfig {

    @Override
    public Class<? extends Dialect> dialect() {
        return H2Dialect.class;
    }

    @Override
    @Bean
    public DataSource dataSource() throws Exception {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/expenses");
        dataSource.setDriverClass(Driver.class);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
