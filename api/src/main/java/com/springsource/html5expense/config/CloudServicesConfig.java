package com.springsource.html5expense.config;

import java.util.Collection;

import javax.sql.DataSource;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.CloudPoolConfiguration;
import org.cloudfoundry.runtime.service.relational.CloudConnectionConfiguration;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

/**
 * @author Josh Long
 */
@Configuration
@Profile("cloud")
public class CloudServicesConfig implements ServicesConfig {

    private String errorMessage = String.format("the  %s collection returned can't be empty. " +
            "Are you sure that you've bound at least one RDBMS of type " +
            "data source to this application?", RdbmsServiceInfo.class.getName() );

    @Override
    public DataSource dataSource() throws Exception {
        RdbmsServiceInfo rdbmsServiceInfo = rdbmsServiceInfo();
        RdbmsServiceCreator rdbmsServiceCreator = new RdbmsServiceCreator();
        CloudPoolConfiguration cloudPoolConfiguration = new CloudPoolConfiguration();
        cloudPoolConfiguration.setPoolSize("1-2"); 
        rdbmsServiceCreator.setCloudPoolConfiguration(cloudPoolConfiguration);        
        return rdbmsServiceCreator.createService(rdbmsServiceInfo);
    }

    @Bean
    public RdbmsServiceInfo rdbmsServiceInfo() {
        Collection<RdbmsServiceInfo> rdbmsServiceInfoList = this.cloudEnvironment().getServiceInfos(RdbmsServiceInfo.class);
        Assert.notEmpty(rdbmsServiceInfoList, this.errorMessage);
        return rdbmsServiceInfoList.iterator().next();
    }

    @Bean
    public CloudEnvironment cloudEnvironment() {
        return new CloudEnvironment();
    }

}