package com.example.springbatch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CustomTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        System.out.println(">>>>>>>>> StepExecution.step2 >>>>>>>>>>");
        // throw new RuntimeException("======= step2 was Failed ======");
        return RepeatStatus.FINISHED;
    }
}
