package com.sparta.publicclassdev;

import static org.mockito.Mockito.mock;

import javax.sql.DataSource;
import org.quartz.Scheduler;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@TestConfiguration
public class TestConfig {
    
    @Bean
    public DataSource dataSource() {
        return mock(DataSource.class);
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        return mock(PlatformTransactionManager.class);
    }
    
    @Bean
    public JobRepository jobRepository() {
        return mock(JobRepository.class);
    }
    
    @Bean
    public org.springframework.batch.core.Job batchJob() {
        return mock(org.springframework.batch.core.Job.class);
    }
    
    @Bean
    public org.quartz.Job quartzJob() {
        return mock(org.quartz.Job.class);
    }
    
    @Bean
    public JobExecution jobExecution() {
        return mock(JobExecution.class);
    }
    
    @Bean
    public Scheduler scheduler() {
        return mock(Scheduler.class);
    }
    
}
