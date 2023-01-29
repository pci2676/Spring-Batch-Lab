package com.javabom.otmp.job.wallet

import com.javabom.otmp.domain.wallet.entity.Wallet
import com.javabom.otmp.domain.wallet.repository.WalletHistoryRepository
import com.javabom.otmp.domain.wallet.repository.WalletRepository
import com.javabom.otmp.test.BatchSpringTest
import com.javabom.otmp.test.TestJobLauncher
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.beans.factory.annotation.Autowired

@BatchSpringTest
internal class WalletChargeBatchConfigurationTest @Autowired constructor(
    private val jobLauncher: TestJobLauncher,
    private val walletRepository: WalletRepository,
    private val walletHistoryRepository: WalletHistoryRepository,
) {

    @AfterEach
    internal fun tearDown() {
        walletHistoryRepository.deleteAll()
        walletHistoryRepository.deleteAll()
    }

    @Test
    fun `파라미터로 넘긴 값만큼 모든 지갑에 돈을 추가한다`() {
        //given
        val wallet1 = Wallet()
        wallet1.update(10)
        val wallet2 = Wallet()
        wallet2.update(20)
        val wallet3 = Wallet()
        wallet3.update(30)
        walletRepository.saveAll(listOf(wallet1, wallet2, wallet3))

        val amount = 1000L
        val jobParameters = JobParametersBuilder()
            .addString("amount", amount.toString())
            .toJobParameters()

        //when
        val jobExecution = jobLauncher.launchJob(WalletChargeBatchConfiguration.JOB_NAME, jobParameters)

        //then
        assertThat(jobExecution.exitStatus).isEqualTo(ExitStatus.COMPLETED)
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)

        val walletHistories1 = walletHistoryRepository.findAllByWallet(wallet1).sortedBy { it.id }
        assertThat(walletHistories1[1].amount).isEqualTo(1000L)
        val walletHistories2 = walletHistoryRepository.findAllByWallet(wallet2).sortedBy { it.id }
        assertThat(walletHistories2[1].amount).isEqualTo(1000L)
        val walletHistories3 = walletHistoryRepository.findAllByWallet(wallet3).sortedBy { it.id }
        assertThat(walletHistories3[1].amount).isEqualTo(1000L)
    }
}
