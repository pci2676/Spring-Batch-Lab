package com.javabom.otmp.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(value = [
    "com.javabom.otmp.domain"
])
@EnableJpaRepositories(value = [
    "com.javabom.otmp.domain"
])
@EnableJpaAuditing
@Configuration
class OtmpCoreConfig
