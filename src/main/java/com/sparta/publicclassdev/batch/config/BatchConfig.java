package com.sparta.publicclassdev.batch.config;

import com.sparta.publicclassdev.batch.tasklet.DailyCodeKataTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DailyCodeKataTasklet dailyCodeKataTasklet;

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
            .tasklet(dailyCodeKataTasklet, transactionManager)
            .build();
    }

    @Bean
    public Job postDailyCodeKata() {
        return new JobBuilder("postDailyCodeKata", jobRepository)
            .start(step1())
            .build();
    }
}
