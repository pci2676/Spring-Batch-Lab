package com.javabom.bombatch.multiprocessing2.infra.entity.repository

import com.javabom.bombatch.multiprocessing2.infra.entity.BalanceSnapShotEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceSnapShotEntityRepository : JpaRepository<BalanceSnapShotEntity, Long> {
    fun findByMemberNumber(memberNumber: String): BalanceSnapShotEntity?
}