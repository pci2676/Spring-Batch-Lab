package com.javabom.bombatch.multiprocessing1.job

import com.javabom.bombatch.multiprocessing1.domain.job.EventNameMigrationBatchConfiguration
import com.javabom.bombatch.multiprocessing1.infra.entity.ServiceEvent
import com.javabom.bombatch.multiprocessing1.infra.entity.repository.ServiceEventJpaRepository
import com.javabom.bombatch.multiprocessing1.test.BatchSpringTest
import com.javabom.bombatch.multiprocessing1.test.TestJobLauncher
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@BatchSpringTest
class EventNameMigrationBatchConfigurationTest(
    private val jobLauncher: TestJobLauncher,
    private val serviceEventJpaRepository: ServiceEventJpaRepository,
) {
    @BeforeEach
    fun setUp() {
        serviceEventJpaRepository.deleteAllInBatch()
    }

    @AfterEach
    fun tearDown() {
        Files.delete(Paths.get("./temp/", "migration.csv"))
    }

    @DisplayName("파일을 이용해서 병렬로 서비스 이벤트의 이름을 업데이트 한다.")
    @Test
    fun test1() {
        //given
        Files.createDirectories(Paths.get("./temp/"))
        val inputStream = """
            uuid1,name1
            uuid2,name2
            uuid3,name3
            uuid4,name4
            uuid5,name5
        """.trimIndent()
            .byteInputStream()
        Files.copy(inputStream, Paths.get("./temp/", "migration.csv"), StandardCopyOption.REPLACE_EXISTING)

        serviceEventJpaRepository.saveAll(
            listOf(
                ServiceEvent(uuid = "uuid1", _name = "UNKNOWN"),
                ServiceEvent(uuid = "uuid2", _name = "UNKNOWN"),
                ServiceEvent(uuid = "uuid3", _name = "UNKNOWN"),
                ServiceEvent(uuid = "uuid4", _name = "UNKNOWN"),
                ServiceEvent(uuid = "uuid5", _name = "UNKNOWN"),
            )
        )

        val jobParameters = JobParametersBuilder()
            .addLong("chunkSize", 2)
            .addLong("poolSize", 2)
            .addString("filePath", "./temp/migration.csv")
            .addString("updatable", "true")
            .toJobParameters()

        //when
        val jobExecution = jobLauncher.launchJob(EventNameMigrationBatchConfiguration.JOB, jobParameters)

        //then
        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)

        val serviceEvents = serviceEventJpaRepository.findAll()
        assertThat(serviceEvents).extracting<String> { it.name }
            .containsExactly(
                "name1",
                "name2",
                "name3",
                "name4",
                "name5",
            )
    }
}
