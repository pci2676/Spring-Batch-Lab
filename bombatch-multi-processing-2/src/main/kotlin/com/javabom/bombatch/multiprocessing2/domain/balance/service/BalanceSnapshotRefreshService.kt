package com.javabom.bombatch.multiprocessing2.domain.balance.service

import com.javabom.bombatch.multiprocessing2.infra.entity.BalanceSnapShotEntity
import com.javabom.bombatch.multiprocessing2.infra.entity.repository.BalanceHistoryEntityRepository
import com.javabom.bombatch.multiprocessing2.infra.entity.repository.BalanceSnapShotEntityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class BalanceSnapshotRefreshService(
    private val historyRepository: BalanceHistoryEntityRepository,
    private val snapShotRepository: BalanceSnapShotEntityRepository,
) {
    fun refresh(memberNumber: String, targetDate: LocalDate): BalanceSnapShotEntity {
        val snapShot: BalanceSnapShotEntity? = snapShotRepository.findByMemberNumber(memberNumber)

        return snapShot?.let {
            //기존 스냅샷이 존재할 때
            val histories = historyRepository.findAllByTradeDateTimeGreaterThanEqualAndTradeDateTimeLessThan(
                startDateTime = it.snapshotDate.atStartOfDay(),
                endDateTime = targetDate.atStartOfDay(),
            )
            val sum = histories.sumOf { history -> history.amount }
            snapShot.accumulate(sum, targetDate)
        } ?: run {
            //새로운 스냅샷을 생성할 때
            val histories = historyRepository.findAllByTradeDateTimeLessThan(targetDate.atStartOfDay())
            val sum = histories.sumOf { history -> history.amount }
            BalanceSnapShotEntity(
                memberNumber = memberNumber,
                amount = sum,
                snapshotDate = targetDate,
            )
        }
    }
}