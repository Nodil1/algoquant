package com.nodil.algoquant.core.managers.money

import com.nodil.algoquant.core.statistic.Statistic

abstract class MoneyManager(
    protected val startMoney: Double
) {
    abstract fun getDealSizeInDollars(statistic: Statistic) : Int
    protected fun getCurrentMoney(statistic: Statistic): Double {
        val earn = statistic.metric.totalEarn
        return startMoney + earn
    }
}