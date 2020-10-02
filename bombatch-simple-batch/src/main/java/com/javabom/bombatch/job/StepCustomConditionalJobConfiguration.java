package com.javabom.bombatch.job;

import com.javabom.bombatch.support.SkipCheckingListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepCustomConditionalJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepCustomConditionalJob() {
        return jobBuilderFactory.get("stepCustomConditionalJob")
                .start(customConditionalStep1())
                .on(ExitStatus.FAILED.getExitCode())
                .end()
                .from(customConditionalStep1())
                .on("COMPLETED WITH SKIPS")
                .to(customConditionalStep3())
                .from(customConditionalStep1())
                .on("*")
                .to(customConditionalStep2())
                .end()
                .build();
    }

    @Bean
    public Step customConditionalStep1() {
        TaskletStep customConditionalStep1 = stepBuilderFactory.get("customConditionalStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> 스탭 1");
                    return RepeatStatus.FINISHED;
                })
                .build();
        customConditionalStep1.registerStepExecutionListener(new SkipCheckingListener());
        return customConditionalStep1;
    }

    @Bean
    public Step customConditionalStep2() {
        return stepBuilderFactory.get("customConditionalStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> 스탭 2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step customConditionalStep3() {
        return stepBuilderFactory.get("customConditionalStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> 스탭 3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
