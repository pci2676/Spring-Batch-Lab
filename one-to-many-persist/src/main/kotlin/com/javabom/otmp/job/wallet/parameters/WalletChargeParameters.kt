package com.javabom.otmp.job.wallet.parameters

import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class WalletChargeParameters {
    @Value("#{jobParameters['amount']}")
    val amount: String? = null

    fun amount() = amount!!.toLong()
}
