package com.sparta.publicclassdev.quartz.config;

import com.sparta.publicclassdev.quartz.job.DailyCodeKataJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail dailyCodeKataDetail() {
        return JobBuilder.newJob(DailyCodeKataJob.class)
            .withIdentity("dailyCodeKataJob")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger dailyCodeKataTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("dailyCodeKataTrigger")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 * * ?"))
            .build();
    }
}
