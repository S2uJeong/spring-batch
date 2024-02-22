package com.example.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobExecutionConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        // Parameter 접근 방법1
                        JobParameters jobParameter = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                        jobParameter.getString("name");
                        jobParameter.getDate("date");
                        // Parameter 접근 방법2
                        chunkContext.getStepContext().getJobParameters();

                        System.out.println("======== JobExecutionConfiguration.Step1 ========");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("======== JobExecutionConfiguration.Step2 ========");
                        //throw new RuntimeException("step2 has failed");  // 1. JobInstance가 생성되고, JobExecution도 생성된다.
                        return RepeatStatus.FINISHED; // 2. 실패했었기 떄문에, 같은 파라미터 값으로 실행해도 에러가 안뜬다. JobExecution이 하나 더 생기고 JobInstance와 M:1 관계가 되었다.
                    }
                })
                .build();
    }
}
