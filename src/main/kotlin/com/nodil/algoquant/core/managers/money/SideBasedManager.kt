package com.nodil.algoquant.core.managers.money

import com.nodil.algoquant.core.statistic.Statistic
import com.nodil.algoquant.core.trader.DealSide

class SideBasedManager(startMoney: Double) : MoneyManager(startMoney) {
    override fun getDealSizeInDollars(statistic: Statistic, dealSide: DealSide): Double {
        if (statistic.size > 10) {
            val sliced = statistic.slice(statistic.size - 10, statistic.size - 1)
            return startMoney * calcRatio(sliced, dealSide)
        }
        return startMoney
    }

    companion object {
        fun calcRatio(statistic: Statistic, dealSide: DealSide): Double {

            var profitLong = statistic.metric.longProfitCount.toDouble()
            if (profitLong == 0.0) profitLong = 1.0

            var profitShort = statistic.metric.shortProfitCount.toDouble()
            if (profitShort == 0.0) profitShort = 1.0

            return if (profitLong > profitShort && dealSide == DealSide.LONG) {
                (profitLong / profitShort) * 2
            } else if (profitLong > profitShort && dealSide == DealSide.SHORT) {
                profitShort / profitLong
            } else if (profitLong < profitShort && dealSide == DealSide.LONG) {
                profitLong / profitShort
            } else if (profitLong < profitShort && dealSide == DealSide.SHORT) {
                (profitShort / profitLong) * 2
            } else {
                1.0
            }
        }
    }
}