package com.javabom.bombatch.multiprocessing2.domain.job

import com.javabom.bombatch.multiprocessing2.infra.entity.BalanceSnapShotEntity
import com.javabom.bombatch.multiprocessing2.infra.entity.MemberEntity
import com.javabom.bombatch.multiprocessing2.infra.entity.repository.BalanceHistoryEntityRepository
import com.javabom.bombatch.multiprocessing2.infra.entity.repository.BalanceSnapShotEntityRepository
import com.javabom.bombatch.multiprocessing2.infra.entity.repository.MemberEntityRepository
import com.javabom.bombatch.multiprocessing2.test.BatchSpringTest
import com.javabom.bombatch.multiprocessing2.test.TestJobLauncher
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import java.io.File
import java.io.FileWriter
import java.time.LocalDate

@BatchSpringTest
class BalanceSnapshotRefreshBatchConfigurationTest(
    private val jobLauncher: TestJobLauncher,
    private val memberRepository: MemberEntityRepository,
    private val balanceHistoryRepository: BalanceHistoryEntityRepository,
    private val balanceSnapShotRepository: BalanceSnapShotEntityRepository,
) {
    @BeforeEach
    fun setUp() {
        balanceSnapShotRepository.deleteAllInBatch()
        balanceHistoryRepository.deleteAllInBatch()
        memberRepository.deleteAllInBatch()
    }

    @DisplayName("스냅샷이 없을때 새롭게 생성한다.")
    @Test
    fun test_29() {
        //given
        File("temp/").mkdirs()
        val filePath = "temp/input.csv"
        val file = File(filePath)
        file.deleteOnExit()
        FileWriter(file).use {
            it.appendLine("Dewitt")
            it.appendLine("Dewitt1")
        }

        val member = memberRepository.save(MemberEntity(memberNumber = "Dewitt"))
        val member1 = memberRepository.save(MemberEntity(memberNumber = "Dewitt1"))

        val jobParameters = JobParametersBuilder()
            .addString("filePath", filePath)
            .addString("chunkSize", "100")
            .addString("targetDate", "2023-02-20")
            .toJobParameters()

        //when
        val jobExecution = jobLauncher.launchJob(BalanceSnapshotRefreshBatchConfiguration.JOB_NAME, jobParameters)

        //then
        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)

        val snapShot = balanceSnapShotRepository.findByMemberNumber(member.memberNumber)
        assertThat(snapShot).isNotNull
    }

    @DisplayName("스냅샷이 있으면 날짜와 값을 갱신한다.")
    @Test
    fun test_40() {
        //given
        val member = memberRepository.save(MemberEntity(memberNumber = "Dewitt"))
        balanceSnapShotRepository.save(BalanceSnapShotEntity(
            memberNumber = member.memberNumber,
            amount = 0L,
            snapshotDate = LocalDate.of(2023, 1, 1))
        )

        val jobParameters = JobParametersBuilder()
            .addString("chunkSize", "10")
            .addString("targetDate", "2023-02-20")
            .toJobParameters()

        //when
        val jobExecution = jobLauncher.launchJob(BalanceSnapshotRefreshBatchConfiguration.JOB_NAME, jobParameters)

        //then
        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)

        val snapShot = balanceSnapShotRepository.findByMemberNumber(member.memberNumber)
        assertThat(snapShot).isNotNull
        assertThat(snapShot!!.snapshotDate).isEqualTo(LocalDate.of(2023, 2, 20))
    }
}
