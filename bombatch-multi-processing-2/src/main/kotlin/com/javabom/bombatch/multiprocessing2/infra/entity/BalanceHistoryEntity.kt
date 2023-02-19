package com.javabom.bombatch.multiprocessing2.infra.entity

import com.javabom.bombatch.multiprocessing2.domain.balance.TradeType
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "balance_history")
class BalanceHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "member_number")
    val memberNumber: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type")
    val tradeType: TradeType,
    @Column(name = "amount")
    val amount: Long,
    @Column(name = "trade_datetime")
    val tradeDateTime: LocalDateTime,
)