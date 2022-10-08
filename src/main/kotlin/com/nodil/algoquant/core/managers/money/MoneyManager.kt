package com.nodil.algoquant.core.managers.money

import com.nodil.algoquant.core.statistic.Statistic
import com.nodil.algoquant.core.trader.Deal
import com.nodil.algoquant.core.trader.DealSide

abstract class MoneyManager(
    protected val startMoney: Double
) {
    abstract fun getDealSizeInDollars(statistic: Statistic, dealSide: DealSide) : Double
    protected fun getCurrentMoney(statistic: Statistic): Double {
        val earn = statistic.metric.totalEarn
        return startMoney + earn
    }
}