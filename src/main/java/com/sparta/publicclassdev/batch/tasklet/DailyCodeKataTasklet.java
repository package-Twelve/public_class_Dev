package com.sparta.publicclassdev.batch.tasklet;

import com.sparta.publicclassdev.domain.codekatas.service.CodeKatasService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyCodeKataTasklet implements Tasklet {

    private final CodeKatasService codeKatasService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext context) {
        codeKatasService.getTodayCodeKata();
        return RepeatStatus.FINISHED;
    }
}
