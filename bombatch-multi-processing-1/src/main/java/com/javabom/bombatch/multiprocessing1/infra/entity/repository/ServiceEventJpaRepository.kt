package com.javabom.bombatch.multiprocessing1.infra.entity.repository

import com.javabom.bombatch.multiprocessing1.infra.entity.ServiceEvent
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceEventJpaRepository : JpaRepository<ServiceEvent, Long>
