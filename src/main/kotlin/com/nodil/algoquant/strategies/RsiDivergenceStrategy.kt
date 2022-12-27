package com.nodil.algoquant.strategies

import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceIndicator
import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceResult
import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceType
import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategyAction
import com.nodil.algoquant.core.strategy.StrategyComment
import com.nodil.algoquant.core.strategy.StrategyResult
import com.nodil.algoquant.core.trader.Target
import com.nodil.algoquant.core.utils.last
import org.ta4j.core.indicators.ATRIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.ZLEMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class RsiDivergenceStrategy(
    override val settings: RsiDivergenceSettings
) : Strategy(settings) {
    private val diverIndicator by lazy {
        RsiDivergenceIndicator(settings.rsiPeriod, settings.lookLeft, settings.lookRight, barSeries) }
    private val closePriceIndicator by lazy { ClosePriceIndicator(barSeries) }

    private val SMA by lazy { ZLEMAIndicator(closePriceIndicator, settings.emaPeriod) }
    private val filterSMA  by lazy { SMAIndicator(closePriceIndicator, 200) }
    private val ATR by lazy { ATRIndicator(barSeries, 14) }


    override fun getResult(): StrategyResult {
        if (barSeries.size < 501) {
            return noSignal()
        }
        val diver = diverIndicator.findDivergence(barSeries)
        if (diver.type == RsiDivergenceType.NONE) return noSignal()

        val smaValue = SMA.last().doubleValue()
        when (diver.type) {
            RsiDivergenceType.BULL -> {
                if (settings.enableSmaFilter && (barSeries.last().close > filterSMA.last().doubleValue())) {
                    return noSignal()
                }
                if ((barSeries.last().close < diver.rsiCurrentBar!!.low) || (barSeries.last().close > diver.rsiPrevBar!!.low) || (barSeries.last().close < smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_LONG,
                    arrayOf(Target(barSeries.last().close + (ATR.last().doubleValue() * settings.takeProfitAtr) , 100)),
                    diver.rsiCurrentBar.low,
                    createComment(diver, smaValue, smaValue)
                )
            }
            RsiDivergenceType.BEAR -> {
                if (settings.enableSmaFilter && (barSeries.last().close < filterSMA.last().doubleValue())) {
                    return noSignal()
                }
                if ((barSeries.last().close > diver.rsiCurrentBar!!.high) || (barSeries.last().close < diver.rsiPrevBar!!.high) || (barSeries.last().close > smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_SHORT,
                    arrayOf(Target(barSeries.last().close - (ATR.last().doubleValue() * settings.takeProfitAtr), 100)),
                    diver.rsiCurrentBar.high,
                    createComment(diver,smaValue, smaValue)
                )
            }
            else -> return noSignal()

        }
    }
    private fun createComment(diver: RsiDivergenceResult, sma1: Double, sma2: Double): StrategyComment {
        val comment = StrategyComment()
        comment["rsiCurrentValue"] = diver.rsiCurrent.toString()
        comment["currentPrice"] = diver.rsiCurrentBar!!.close.toString()
        comment["currentTime"] = diver.rsiCurrentBar.getDateTime().toString()
        comment["rsiPrevValue"] = diver.rsiPrev.toString()
        comment["prevPrice"] = diver.rsiPrevBar!!.close.toString()
        comment["prevTime"] = diver.rsiPrevBar.getDateTime().toString()
        comment["sma1"] = sma1.toString()
        comment["sma2"] = sma2.toString()

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