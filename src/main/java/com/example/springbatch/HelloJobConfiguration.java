package com.example.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class HelloJobConfiguration {

    @Bean
    public Job helloJob(JobRepository jobRepository, Step helloStep1, Step helloStep2) {
        /** JobBuilder : Job을 만든다.
         *          파라미터 : JobRepository
         *           스트림 : step들
         */
        return new JobBuilder("HelloJob",jobRepository)
                .start(helloStep1)
                .next(helloStep2)
                .build();
    }
    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager ptm) {
        /**
         * StepBuilder : Step을 만든다.
         *     파라미터 : JobRepository
         *      스트림 : tasklet((contribution, chunkContect)) -> return RepeatStatus.상태상수 ,  PlatformTransactionManager)
         */
        return new StepBuilder("HelloStep2", jobRepository)
                // tasklet >> 실제 실행하고자 하는 비즈니스 로직 작성
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("====================================");
                    System.out.println(" helloStep2 executed ");
                    System.out.println("====================================");
                    return RepeatStatus.FINISHED;
                }, ptm).build();
    }

    @Bean
    public Step helloStep1 (JobRepository jobRepository, PlatformTransactionManager ptm) {
        return new StepBuilder("HelloStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("====================================");
                    System.out.println(" helloStep1 executed ");
                    System.out.println("====================================");
                    return RepeatStatus.FINISHED;
                }, ptm).build();
    }

}
