/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.html5expense.config;

import java.io.File;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang.SystemUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springsource.html5expense.EligibleCharge;
import com.springsource.html5expense.Expense;
import com.springsource.html5expense.ExpenseReport;
import com.springsource.html5expense.services.JpaExpenseReportingService;

/**
 * Configuration for application @Components such as @Services, @Repositories,
 * and @Controllers. Loads externalized property values required to configure
 * the various application properties. Not much else here, as we rely on @Component
 * scanning in conjunction with @Inject by-type autowiring.
 * 
 * @author Keith Donald
 * @author Josh Long
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackageClasses = JpaExpenseReportingService.class)
@Import({ CloudServicesConfig.class, LocalServicesConfig.class })
public class ComponentConfig {

	static private Class<?>[] entityClasses = { Expense.class, ExpenseReport.class, EligibleCharge.class };

	@Autowired
	private ServicesConfig servicesConfig;

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		EntityManagerFactory emf = entityManagerFactory().getObject();
		return new JpaTransactionManager(emf);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put("hibernate.dialect", MySQL5InnoDBDialect.class.getName());		
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setJpaPropertyMap(properties);
		factory.setDataSource(this.servicesConfig.dataSource());
		factory.setPackagesToScan(packagesFromEntityClasses(entityClasses));
		return factory;
	}

	private String[] packagesFromEntityClasses(Class<?>[] classes) {
		Set<String> pkgs = new HashSet<String>();
		for (Class<?> c : classes)
			pkgs.add(c.getPackage().getName());
		return new ArrayList<String>(pkgs).toArray(new String[pkgs.size()]);
	}
}
