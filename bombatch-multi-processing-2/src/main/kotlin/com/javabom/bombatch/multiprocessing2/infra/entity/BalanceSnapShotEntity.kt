package com.javabom.bombatch.multiprocessing2.infra.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(
    name = "balance_snapshot",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_balance_snapshot_1", columnNames = ["member_number"])
    ]
)
class BalanceSnapShotEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "member_number")
    val memberNumber: String,
    @Column(name = "amount")
    var amount: Long,
    @Column(name = "snapshot_date")
    var snapshotDate: LocalDate,
) {
    fun accumulate(sum: Long, snapshotDate: LocalDate): BalanceSnapShotEntity {
        this.amount += sum
        this.snapshotDate = snapshotDate
        return this
    }
}