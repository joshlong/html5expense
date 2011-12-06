package com.springsource.html5expense.config.batch;

import com.springsource.html5expense.config.EnvironmentAwareApplicationContextInitializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);

    public static void main(String args[]) throws Exception {

        EnvironmentAwareApplicationContextInitializer aci = new EnvironmentAwareApplicationContextInitializer();

        //AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("/ec-loader.xml");
        aci.initialize(ac);
         ac.refresh();

        log.debug(String.format("active profile: %s", StringUtils.join(ac.getEnvironment().getActiveProfiles(), ",")));

        Job job = ac.getBean("read-eligible-charges", Job.class);

        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("file", "foo");
        builder.addLong("uid", System.currentTimeMillis());
        JobParameters jobParameters = builder.toJobParameters();

        JobLauncher jobLauncher = ac.getBean(JobLauncher.class);
        jobLauncher.run(job, jobParameters);

    }
}
