package com.javabom.otmp.domain.wallet.repository

import com.javabom.otmp.domain.wallet.entity.Wallet
import com.javabom.otmp.domain.wallet.entity.WalletHistory
import org.springframework.data.jpa.repository.JpaRepository

interface WalletHistoryRepository : JpaRepository<WalletHistory, Long> {
    fun findAllByWallet(wallet: Wallet): List<WalletHistory>
}
