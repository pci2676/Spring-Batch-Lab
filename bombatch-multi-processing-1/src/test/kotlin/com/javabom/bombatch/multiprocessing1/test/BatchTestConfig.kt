package com.javabom.bombatch.multiprocessing1.test

import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BatchTestConfig {

    @Bean
    fun testJobLauncher(
        applicationContext: ApplicationContext,
        jobRepository: JobRepository,
        jobLauncher: JobLauncher,
    ): TestJobLauncher {
        return TestJobLauncher(applicationContext, jobRepository, jobLauncher)
    }

}
