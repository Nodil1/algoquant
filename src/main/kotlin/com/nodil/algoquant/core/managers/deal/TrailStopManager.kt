package com.nodil.algoquant.core.managers.deal

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.indicators.ta4j.SourceType
import com.nodil.algoquant.core.indicators.ta4j.WrappedIndicator
import com.nodil.algoquant.core.trader.Deal
import com.nodil.algoquant.core.trader.DealSide
import org.ta4j.core.indicators.ATRIndicator

class TrailStopManager(val coef: Int = 2) : DealManager() {
    val ATR = WrappedIndicator(ATRIndicator::class.java, 14)

    override fun checkDeal(barSeries: com.nodil.algoquant.core.bars.BarSeries, deal: Deal) {
        val sliced = barSeries.getLast(30)
        ATR.calculate(sliced, SourceType.SERIES)
        val range = ATR.last * coef
        val lastPrice = barSeries.last().close
        when (deal.side) {
            DealSide.LONG -> {
                if ((lastPrice - range) > deal.stopLoss) {
                    deal.stopLoss = lastPrice - range
                }
            }
            DealSide.SHORT -> {
                if (lastPrice + range < deal.stopLoss) {
                    deal.stopLoss = lastPrice + range
                }
            }
        }
    }
}