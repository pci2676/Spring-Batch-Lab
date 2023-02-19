package com.javabom.bombatch.multiprocessing2.domain.job.parameters

import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@JobScope
@Configuration
class BalanceSnapshotRefreshJobParameters {
    @Value("#{jobParameters['chunkSize']}")
    private lateinit var _chunkSize: String

    @Value("#{jobParameters['targetDate']}")
    private lateinit var _targetDate: String

    val chunkSize: Long
        get() = _chunkSize!!.toLong()

    val targetDate: LocalDate
        get() = LocalDate.parse(_targetDate!!)
}