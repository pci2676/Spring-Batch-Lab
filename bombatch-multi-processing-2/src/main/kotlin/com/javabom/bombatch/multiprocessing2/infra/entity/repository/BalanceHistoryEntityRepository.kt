package com.javabom.bombatch.multiprocessing2.infra.entity.repository

import com.javabom.bombatch.multiprocessing2.infra.entity.BalanceHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BalanceHistoryEntityRepository : JpaRepository<BalanceHistoryEntity, Long> {
    fun findAllByTradeDateTimeGreaterThanEqualAndTradeDateTimeLessThan(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<BalanceHistoryEntity>

    fun findAllByTradeDateTimeLessThan(endDateTime: LocalDateTime): List<BalanceHistoryEntity>
}