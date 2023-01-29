package com.javabom.bombatch.multiprocessing1.domain.job

import com.javabom.bombatch.multiprocessing1.domain.job.parameter.EventNameMigrationJobParameters
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.JobSynchronizationManager
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.io.File
import javax.sql.DataSource

@Configuration
class EventNameMigrationBatchConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource,
    private val jobParameters: EventNameMigrationJobParameters,
) {
    val log = KotlinLogging.logger(EventNameMigrationBatchConfiguration::class.java.name)

    @Bean(JOB)
    fun job(): Job {
        return jobBuilderFactory[JOB]
            .preventRestart()
            .start(step())
            .build()
    }

    @JobScope
    @Bean(STEP)
    fun step(): Step {
        return stepBuilderFactory[STEP]
            .chunk<UpdateDto, UpdateDto>(jobParameters.chunkSize)
            .reader(reader())
            .processor(
                processor(
                    //null
                )
            )
            .writer(writer())
            .taskExecutor(taskExecutor())
            .throttleLimit(jobParameters.poolSize)
            .build()
    }

    @StepScope
    @Bean(READER)
    fun reader(): FlatFileItemReader<UpdateDto> {
        val lineMapper: DefaultLineMapper<UpdateDto> = DefaultLineMapper<UpdateDto>()
            .apply {
                setLineTokenizer(
                    DelimitedLineTokenizer(",")
                        .apply { setNames("uuid", "name") }
                )
                setFieldSetMapper(
                    BeanWrapperFieldSetMapper<UpdateDto>()
                        .apply { setTargetType(UpdateDto::class.java) }
                )
            }

        return FlatFileItemReaderBuilder<UpdateDto>()
            .lineMapper(lineMapper)
            .resource(FileSystemResource(File(jobParameters.filePath)))
            .saveState(false)
            .build()
    }

    @StepScope
    @Bean(PROCESSOR)
    fun processor(
//        @Value("#{jobParameters['updatable']}") updatable: Boolean?
    ): ItemProcessor<UpdateDto, UpdateDto> {
        return ItemProcessor {
            log.info { "item: $it" }
            return@ItemProcessor if (jobParameters.updatable) {
                it
            } else {
                null
            }
        }
    }

    @StepScope
    @Bean(WRITER)
    fun writer(): ItemWriter<UpdateDto> {
        return JdbcBatchItemWriterBuilder<UpdateDto>()
            .sql("UPDATE service_event SET name = ? WHERE uuid = ?")
            .dataSource(dataSource)
            .itemPreparedStatementSetter { item, ps ->
                ps.setString(1, item.name)
                ps.setString(2, item.uuid)
            }
            .build()
    }

    @JobScope
    @Bean(TASK_EXECUTOR)
    fun taskExecutor(): TaskExecutor {
        val jobExecution = JobSynchronizationManager.getContext()!!.jobExecution
        return ThreadPoolTaskExecutor()
            .apply {
                corePoolSize = jobParameters.poolSize
                maxPoolSize = jobParameters.poolSize
                setThreadNamePrefix("event-name-migration-thread-")
                setAwaitTerminationSeconds(10)
                setQueueCapacity(Integer.MAX_VALUE)
                setWaitForTasksToCompleteOnShutdown(true)
                setTaskDecorator {
                    Runnable {
                        JobSynchronizationManager.register(jobExecution)
                        try {
                            it.run()
                        } catch (e: Exception) {
                            log.error { e }
                        } finally {
                            JobSynchronizationManager.close()
                        }
                    }
                }
            }
    }

    data class UpdateDto(
        var uuid: String? = null,
        var name: String? = null,
    )

    companion object {
        const val JOB = "EventNameMigrationBatch"
        const val STEP = "${JOB}Step"
        const val READER = "${STEP}Reader"
        const val PROCESSOR = "${STEP}Processor"
        const val WRITER = "${STEP}Writer"
        const val TASK_EXECUTOR = "${JOB}TaskExecutor"
    }
}
