package com.javabom.otmp.domain.wallet.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "wallet_history")
class WalletHistory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false, columnDefinition = "bigint COMMENT '지갑 id'")
    val wallet: Wallet,

    @Column(name = "amount", columnDefinition = "BIGINT COMMENT '변화량'")
    val amount: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
        protected set
}
