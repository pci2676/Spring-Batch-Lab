package com.javabom.otmp.job.wallet

import com.javabom.otmp.domain.wallet.entity.QWallet.wallet
import com.javabom.otmp.domain.wallet.entity.QWalletHistory.walletHistory
import com.javabom.otmp.domain.wallet.entity.Wallet
import com.javabom.otmp.domain.wallet.repository.WalletRepository
import com.javabom.otmp.job.wallet.parameters.WalletChargeParameters
import com.javabom.otmp.module.QuerydslCursorItemReader
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

@Configuration
class WalletChargeBatchConfiguration(
    private val jobFactory: JobBuilderFactory,
    private val stepFactory: StepBuilderFactory,
    @PersistenceUnit
    private val entityManagerFactory: EntityManagerFactory,
    private val jobParameters: WalletChargeParameters,
    private val walletRepository: WalletRepository,
) {

    @Bean(name = [JOB_NAME])
    fun walletChargeJob(): Job = jobFactory[JOB_NAME]
        .preventRestart()
        .start(walletChargeStep())
        .build()

    @JobScope
    @Bean(name = [CHARGE_STEP])
    fun walletChargeStep(): Step = stepFactory[CHARGE_STEP]
        .chunk<Wallet, Wallet>(300)
        .reader(walletChargeReader())
        .processor(walletChargeProcessor())
        .writer(walletChargeWriter())
        .build()

    @Bean(name = [CHARGE_READER])
    fun walletChargeReader(): QuerydslCursorItemReader<Wallet> {
        return QuerydslCursorItemReader(entityManagerFactory, 300) { query ->
            query.selectFrom(wallet)
                .distinct()
                .leftJoin(wallet.walletHistories, walletHistory).fetchJoin()
                .orderBy(wallet.id.asc())
        }
    }

    @StepScope
    @Bean(name = [CHARGE_PROCESSOR])
    fun walletChargeProcessor(): ItemProcessor<Wallet, Wallet> {
        val amount = jobParameters.amount()
        return ItemProcessor {
            it.apply { update(amount) }
        }
    }

    @Bean(name = [CHARGE_WRITER])
    fun walletChargeWriter(): ItemWriter<Wallet> {
        return ItemWriter {
            println("""
                
                wallets ids = ${it.map { w -> w.id }}
            """.trimIndent())
            walletRepository.saveAll(it)
        }
    }

    companion object {
        const val JOB_NAME = "WalletChargeBatch"
        const val CHARGE_STEP = "${JOB_NAME}ChargeStep"
        const val CHARGE_READER = "${CHARGE_STEP}Reader"
        const val CHARGE_PROCESSOR = "${CHARGE_STEP}Processor"
        const val CHARGE_WRITER = "${CHARGE_STEP}Writer"
    }
}
