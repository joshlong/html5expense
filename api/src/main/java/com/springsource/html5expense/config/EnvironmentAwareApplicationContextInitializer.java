package com.springsource.html5expense.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 * The {@link ApplicationContextInitializer application context initializer} is a callback interface that Spring
 * provides that let's you configure the {@link org.springframework.context.ApplicationContext application context}.
 * You can configure which environment you'd like to be active by setting
 * {@link org.springframework.core.env.ConfigurableEnvironment#setActiveProfiles(String...) the active profile} based on
 * conditions that you dictate. In our case, we'll assume that we're running locally, outside of Cloud Foundry, if
 * {@link org.cloudfoundry.runtime.env.CloudEnvironment#isCloudFoundry()}  returns false, and otherwise, assume we're
 * running inside of Cloud Foundry and act accordingly.
 *
 * @author Josh Long
 */
public class EnvironmentAwareApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private Log log = LogFactory.getLog(getClass());
    private CloudEnvironment cloudEnvironment = new CloudEnvironment();

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (cloudEnvironment.isCloudFoundry()) {
            // then we're running inside Cloud Foundry
            if(log.isInfoEnabled()) log.info( "setting active profile to 'cloud'.");
            applicationContext.getEnvironment().setActiveProfiles("cloud");
        } else {
            // otherwise we're running in our local environment
            if(log.isInfoEnabled()) log.info( "setting active profile to 'local'.");
            applicationContext.getEnvironment().setActiveProfiles("local");
        }
    }
}
