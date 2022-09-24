package com.algoquant.strategies

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceIndicator
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceResult
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceType
import com.algoquant.core.indicators.ta4j.WrappedIndicator
import com.algoquant.core.strategy.Strategy
import com.algoquant.core.strategy.StrategyAction
import com.algoquant.core.strategy.StrategyComment
import com.algoquant.core.strategy.StrategyResult
import com.algoquant.core.trader.Target
import org.ta4j.core.indicators.EMAIndicator

class RsiDivergenceStrategy(
    var settings: RsiDivergenceSettings
) : Strategy() {
    private val diverIndicator =
        RsiDivergenceIndicator(settings.rsiPeriod, settings.lookLeft, settings.lookRight)
    private val SMA = WrappedIndicator(EMAIndicator::class.java, settings.emaPeriod)
    override fun getResult(barSeries: BarSeries): StrategyResult {
        if (barSeries.size < 50) {
            return noSignal()
        }
        val diver = diverIndicator.findDivergence(barSeries)
        val sliced = barSeries.getLast(30)
        SMA.calculate(sliced)
        val smaValue = SMA.get(sliced.size-1).doubleValue()
        when (diver.type) {
            RsiDivergenceType.BULL -> {
                if ((barSeries.last().close < diver.rsiCurrentBar!!.low) || (barSeries.last().close > diver.rsiPrevBar!!.low) || (barSeries.last().close < smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_LONG,
                    arrayOf(Target(barSeries.last().close + (barSeries.last().close * settings.takeProfit) , 100)),
                    diver.rsiCurrentBar.low,
                    createComment(diver)
                )
            }
            RsiDivergenceType.BEAR -> {
                if ((barSeries.last().close > diver.rsiCurrentBar!!.high) || (barSeries.last().close < diver.rsiPrevBar!!.high) || (barSeries.last().close > smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_SHORT,
                    arrayOf(Target(barSeries.last().close - (barSeries.last().close * settings.takeProfit), 100)),
                    diver.rsiCurrentBar.high,
                    createComment(diver)
                )
            }
            else -> return noSignal()

        }
    }
    private fun createComment(diver: RsiDivergenceResult): StrategyComment {
        val comment = StrategyComment()
        comment["rsiCurrentValue"] = diver.rsiCurrent.toString()
        comment["currentPrice"] = diver.rsiCurrentBar!!.close.toString()
        comment["currentTime"] = diver.rsiCurrentBar!!.getDateTime().toString()
        comment["rsiPrevValue"] = diver.rsiPrev.toString()
        comment["prevPrice"] = diver.rsiPrevBar!!.close.toString()
        comment["prevTime"] = diver.rsiPrevBar!!.getDateTime().toString()
        return comment
    }
    companion object {
        fun generate(): Array<RsiDivergenceStrategy> {
            val result = mutableListOf<RsiDivergenceStrategy>()
            RsiDivergenceSettings.generate().onEach {
                result.add(RsiDivergenceStrategy(it))
            }
            return result.toTypedArray()
        }
    }
}