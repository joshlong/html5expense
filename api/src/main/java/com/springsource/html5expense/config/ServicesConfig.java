package com.springsource.html5expense.config;

import javax.sql.DataSource;

/**
 *
 * Provides definitons for the Hibernate {@link org.hibernate.dialect.Dialect} and the SQL RDBMS {@link DataSource}.
 *
 * @author Josh Long
 */
public interface ServicesConfig {

    /**
     * which dialect does the {@link DataSource} required?
     *
     * @return a Hibernate dialect to be used in configuring the {@link org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean}
     */
    Class<? extends org.hibernate.dialect.Dialect> dialect();


    /**
     * a SQL RDBMS data source
     *
     * @return the SQL RDBMS data source reference
     * @throws Exception there are often issues in configuring a DataSource
     */
    DataSource dataSource() throws Exception;

}
