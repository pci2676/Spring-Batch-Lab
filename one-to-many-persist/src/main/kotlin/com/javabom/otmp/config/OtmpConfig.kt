package com.javabom.otmp.config

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableBatchProcessing
@Import(
    OtmpCoreConfig::class,
    QuerydslConfig::class,
)
@ComponentScan(
    "com.javabom.otmp.domain",
    "com.javabom.otmp.job",
)
class OtmpConfig
