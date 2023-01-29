package com.javabom.bombatch.multiprocessing1.domain.job.parameter

import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class EventNameMigrationJobParameters(
    @Autowired
    @Value("#{jobParameters['chunkSize']}")
    private var _chunkSize: Int? = null,

    @Autowired
    @Value("#{jobParameters['poolSize']}")
    private var _poolSize: Int? = null,

    @Autowired
    @Value("#{jobParameters['filePath']}")
    private var _filePath: String? = null,

    @Autowired
    @Value("#{jobParameters['updatable']}")
    private var _updatable: Boolean? = null,
) {
    val chunkSize: Int
        get() = _chunkSize!!

    val poolSize: Int
        get() = _poolSize!!

    val filePath: String
        get() = _filePath!!

    val updatable: Boolean
        get() = _updatable == true
}
