package com.sparta.publicclassdev.quartz.job;

import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
import com.sparta.publicclassdev.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyCodeKataJob implements Job {


    private final CodeKatasService codeKatasService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SecurityUtil.setAdminRole();
        try {
            codeKatasService.createRandomCodeKata();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }

    }
}
