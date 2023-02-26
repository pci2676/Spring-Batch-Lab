package com.javabom.bombatch.multiprocessing2.domain.job

import com.javabom.bombatch.multiprocessing2.domain.balance.service.BalanceSnapshotRefreshService
import com.javabom.bombatch.multiprocessing2.domain.job.parameters.BalanceSnapshotRefreshJobParameters
import com.javabom.bombatch.multiprocessing2.infra.entity.BalanceSnapShotEntity
import com.javabom.bombatch.multiprocessing2.infra.entity.MemberEntity
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.io.File
import java.time.LocalDate
import javax.persistence.EntityManagerFactory


@Configuration
class BalanceSnapshotRefreshBatchConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val jobParameters: BalanceSnapshotRefreshJobParameters,
    private val service: BalanceSnapshotRefreshService,
) {

    @Bean(JOB_NAME)
    fun job(): Job = jobBuilderFactory[JOB_NAME]
        .preventRestart()
        .start(step())
        .build()

    @JobScope
    @Bean(STEP_NAME)
    fun step(): Step = stepBuilderFactory[STEP_NAME]
        .chunk<String, BalanceSnapShotEntity>(jobParameters.chunkSize.toInt())
        .reader(reader())
        .processor(processor(null))
        .writer(writer())
        .throttleLimit(jobParameters.chunkSize.toInt())
        .taskExecutor(taskExecutor())
        .build()


    @StepScope
    @Bean(STEP_READER)
    fun reader(): FlatFileItemReader<String> {
        return FlatFileItemReaderBuilder<String>()
            .name(STEP_READER)
            .lineMapper { line, _ -> line }
            .saveState(false)
            .resource(FileSystemResource(File(jobParameters.filePath)))
            .build()
    }

    @StepScope
    @Bean(STEP_PROCESSOR)
    fun processor(@Value("#{jobParameters['targetDate']}") targetDate: String?): ItemProcessor<String, BalanceSnapShotEntity> {
        return ItemProcessor {
            service.refresh(it, LocalDate.parse(targetDate))
        }
    }

    @Bean(STEP_WRITER)
    fun writer(): JpaItemWriter<BalanceSnapShotEntity> {
        return JpaItemWriterBuilder<BalanceSnapShotEntity>()
            .entityManagerFactory(entityManagerFactory)
            .usePersist(false)
            .build()
    }

    @JobScope
    @Bean(TASK_EXECUTOR_NAME)
    fun taskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = jobParameters.chunkSize.toInt()
        executor.maxPoolSize = jobParameters.chunkSize.toInt()
        executor.threadNamePrefix = "refresh-thread-"
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.initialize()
        return executor
    }

    companion object {
        const val JOB_NAME = "BalanceSnapshotRefreshBatch"
        const val STEP_NAME = "${JOB_NAME}Step"
        const val STEP_READER = "${STEP_NAME}Reader"
        const val STEP_PROCESSOR = "${STEP_NAME}Processor"
        const val STEP_WRITER = "${STEP_NAME}Writer"
        const val TASK_EXECUTOR_NAME = "${STEP_NAME}TaskExecutor"

    }
}