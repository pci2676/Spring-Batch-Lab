package com.javabom.bombatch.job;

import com.javabom.bombatch.tasklet.SimpleScopeJobTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleScopeJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SimpleScopeJobTasklet simpleScopeJobTasklet;

    @Bean
    public Job simpleScopeJob() {
        return jobBuilderFactory.get("simpleScopeJob")
                .start(simpleScopeStep1())
                .build();
    }

    @Bean
    @JobScope
    public Step simpleScopeStep1() {
        return stepBuilderFactory.get("simpleScopeStep1")
                .tasklet(simpleScopeJobTasklet)
                .build();
    }

}
