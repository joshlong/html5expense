package com.springsource.html5expense.config;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;

@Configuration
@Profile("local")
public class LocalServicesConfig implements ServicesConfig {

	@Override
	@Bean
	public DataSource dataSource() throws Exception {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setUrl("jdbc:mysql://localhost/expenses");
		dataSource.setDriverClass(Driver.class);
		dataSource.setUsername("expenses");
		dataSource.setPassword("expenses");
		return dataSource;
	}
}
