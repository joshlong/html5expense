package com.springsource.html5expense.config.batch;

import com.springsource.html5expense.config.CloudServicesConfig;
import com.springsource.html5expense.config.ComponentConfig;
import com.springsource.html5expense.config.LocalServicesConfig;
import com.springsource.html5expense.config.ServicesConfig;
import com.springsource.html5expense.integrations.EligibleChargeProcessor;
import com.springsource.html5expense.integrations.EligibleChargeProcessorHeaders;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * This Spring Batch job lets you batch load information into the database based on
 * a CSV file (in this configuration, the file's supposed to be loaded from {$HOME/in}).
 *
 * @author Josh Long
 */
@Configuration
@Import({ComponentConfig.class ,CloudServicesConfig.class, LocalServicesConfig.class})
@ImportResource("/ec-loader.xml")
public class BatchConfig {

    @Inject private ServicesConfig servicesConfig ;
    @Inject private ComponentConfig componentConfig;

    @Autowired
    @Qualifier("newEligibleCharges")
    private MessageChannel channel;

    private File batchFileDirectory;

    @Autowired
    public void setBatchFileDirectory(@Value("#{ systemProperties['user.home'] }") String userHome) throws Exception {
        batchFileDirectory = new File(userHome, "in");
        if (!batchFileDirectory.exists())
            batchFileDirectory.mkdirs();

    }

    @Bean
    public SimpleJobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository((JobRepository) this.jobRepository().getObject());
        return jobLauncher;
    }

    @Bean
    public EligibleChargeProcessor eligibleChargeProcessor() {
        return new EligibleChargeProcessor();
    }

    @Bean
    @Scope("step")
    public FlatFileItemReader reader(@Value("#{jobParameters[file]}") String resource) {

        File f = new File(this.batchFileDirectory, resource + ".csv");

        DelimitedLineTokenizer del = new DelimitedLineTokenizer();
        del.setNames("date,amount,category,merchant".split(","));
        del.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);

        DefaultLineMapper<FieldSet> defaultLineMapper = new DefaultLineMapper<FieldSet>();
        defaultLineMapper.setLineTokenizer(del);
        defaultLineMapper.setFieldSetMapper(new PassThroughFieldSetMapper());
        defaultLineMapper.afterPropertiesSet();

        FlatFileItemReader<FieldSet> fileItemReader = new FlatFileItemReader<FieldSet>();
        fileItemReader.setLineMapper(defaultLineMapper);
        fileItemReader.setResource(new FileSystemResource(f));

        return fileItemReader;
    }

    @Bean
    public ItemWriter writer() {
        return new MessageSendingItemWriter(this.channel);
    }

    @Bean
    public JobRepositoryFactoryBean jobRepository() throws Exception {
        JobRepositoryFactoryBean bean = new JobRepositoryFactoryBean();
        bean.setTransactionManager(new DataSourceTransactionManager(servicesConfig .dataSource()));
        bean.setDataSource(servicesConfig .dataSource());
        return bean;
    }

    public static class MessageSendingItemWriter implements ItemWriter<DefaultFieldSet> {

        private MessageChannel channel;

        public MessageSendingItemWriter(MessageChannel channel) {
            this.channel = channel;
        }

        @Override
        public void write(List<? extends DefaultFieldSet> defaultFieldSets) throws Exception {
            for (DefaultFieldSet defaultFieldSet : defaultFieldSets) {
                Date date = defaultFieldSet.readDate(0);
                BigDecimal bigDecimal = defaultFieldSet.readBigDecimal(1);
                String category = defaultFieldSet.readString(2);
                String merchant = defaultFieldSet.readString(3);

                Message msg = MessageBuilder.withPayload(category)
                        .setHeader(EligibleChargeProcessorHeaders.EC_AMOUNT, bigDecimal)
                        .setHeader(EligibleChargeProcessorHeaders.EC_CATEGORY, category)
                        .setHeader(EligibleChargeProcessorHeaders.EC_MERCHANT, merchant)
                        .setHeader(EligibleChargeProcessorHeaders.EC_DATE, date)
                        .build();
                this.channel.send(msg);

            }
        }
    }

}
