package com.springsource.html5expense.config.batch;

import com.springsource.html5expense.config.EnvironmentAwareApplicationContextInitializer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String args[]) throws Exception {
        EnvironmentAwareApplicationContextInitializer environmentAwareApplicationContextInitializer = new EnvironmentAwareApplicationContextInitializer();
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(BatchConfig.class);
        environmentAwareApplicationContextInitializer.initialize(annotationConfigApplicationContext);

        Job job = annotationConfigApplicationContext.getBean("read-eligible-charges", Job.class);

        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("file", "foo");
        builder.addLong("uid", System.currentTimeMillis());
        JobParameters jobParameters = builder.toJobParameters();

        JobLauncher jobLauncher = annotationConfigApplicationContext.getBean(JobLauncher.class);
        jobLauncher.run(job, jobParameters);

    }
}
