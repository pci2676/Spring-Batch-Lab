package com.javabom.otmp.domain.wallet.repository

import com.javabom.otmp.domain.wallet.entity.Wallet
import org.springframework.data.jpa.repository.JpaRepository

interface WalletRepository : JpaRepository<Wallet, Long>
