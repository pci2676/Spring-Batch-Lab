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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        .chunk<MemberEntity, BalanceSnapShotEntity>(jobParameters.chunkSize.toInt())
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build()


    @StepScope
    @Bean(STEP_READER)
    fun reader(): JpaPagingItemReader<MemberEntity> {
        return JpaPagingItemReaderBuilder<MemberEntity>()
            .name(STEP_READER)
            .queryString("SELECT m FROM MemberEntity m ORDER BY id ASC")
            .parameterValues(emptyMap())
            .pageSize(jobParameters.chunkSize.toInt())
            .entityManagerFactory(entityManagerFactory)
            .saveState(false)
            .build()
    }

    @StepScope
    @Bean(STEP_PROCESSOR)
    fun processor(): ItemProcessor<MemberEntity, BalanceSnapShotEntity> {
        return ItemProcessor {
            service.refresh(it.memberNumber, jobParameters.targetDate)
        }
    }

    @Bean(STEP_WRITER)
    fun writer(): JpaItemWriter<BalanceSnapShotEntity> {
        return JpaItemWriterBuilder<BalanceSnapShotEntity>()
            .entityManagerFactory(entityManagerFactory)
            .usePersist(false)
            .build()
    }

    companion object {
        const val JOB_NAME = "BalanceSnapshotRefreshBatch"
        const val STEP_NAME = "${JOB_NAME}Step"
        const val STEP_READER = "${STEP_NAME}Reader"
        const val STEP_PROCESSOR = "${STEP_NAME}Processor"
        const val STEP_WRITER = "${STEP_NAME}Writer"
    }
}