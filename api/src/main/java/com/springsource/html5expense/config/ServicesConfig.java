package com.springsource.html5expense.config;

import javax.sql.DataSource;

/**
 *
 * Provides definitions for the Hibernate {@link org.hibernate.dialect.Dialect} and the SQL RDBMS {@link DataSource}.
 *
 * @author Josh Long
 */
public interface ServicesConfig {
	
    /**
     * a SQL RDBMS data source
     *
     * @return the SQL RDBMS data source reference
     * @throws Exception there are often issues in configuring a DataSource
     */
    DataSource dataSource() throws Exception;

}
