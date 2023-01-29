package com.javabom.bombatch.multiprocessing1.config

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableBatchProcessing
@EntityScan(
    value = [
        "com.javabom.bombatch.multiprocessing1.infra.entity",
    ]
)
@EnableJpaRepositories(
    value = [
        "com.javabom.bombatch.multiprocessing1.infra.entity",
    ]
)
@EnableJpaAuditing
@ComponentScan(
    "com.javabom.bombatch.multiprocessing1",
    "com.javabom.bombatch.multiprocessing1",
)
class ApplicationConfig
