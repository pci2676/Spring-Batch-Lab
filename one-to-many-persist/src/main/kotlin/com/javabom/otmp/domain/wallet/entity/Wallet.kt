package com.javabom.otmp.domain.wallet.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "wallet")
class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null
        protected set

    @OneToMany(
        mappedBy = "wallet",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE],
        fetch = FetchType.LAZY,
    )
    val walletHistories: MutableSet<WalletHistory> = mutableSetOf()

    @Column(name = "balance", columnDefinition = "BIGINT COMMENT '잔고'")
    var balance: Long = 0L
        protected set

    fun update(updateAmount: Long): WalletHistory {
        this.balance += updateAmount

        return WalletHistory(this, updateAmount).apply {
            walletHistories.add(this)
        }
    }
}
