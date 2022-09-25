package com.algoquant.strategies

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceIndicator
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceResult
import com.algoquant.core.indicators.rsi.divergence.RsiDivergenceType
import com.algoquant.core.indicators.ta4j.SourceType
import com.algoquant.core.indicators.ta4j.WrappedIndicator
import com.algoquant.core.strategy.Strategy
import com.algoquant.core.strategy.StrategyAction
import com.algoquant.core.strategy.StrategyComment
import com.algoquant.core.strategy.StrategyResult
import com.algoquant.core.trader.Target
import org.ta4j.core.indicators.ATRIndicator
import org.ta4j.core.indicators.EMAIndicator
import org.ta4j.core.indicators.HMAIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class RsiDivergenceStrategy(
    var settings: RsiDivergenceSettings
) : Strategy() {
    private val diverIndicator =
        RsiDivergenceIndicator(settings.rsiPeriod, settings.lookLeft, settings.lookRight)

    private val localBarSeries = BarSeries()

    lateinit var closePrice : ClosePriceIndicator
    lateinit var SMA : EMAIndicator
    lateinit var filterSMA : EMAIndicator
    lateinit var ATR : ATRIndicator

    private fun initIndicators(){
        closePrice = ClosePriceIndicator(localBarSeries)
        SMA = EMAIndicator(closePrice, 5)
        filterSMA = EMAIndicator(closePrice, 10)
        ATR = ATRIndicator(localBarSeries, 14)
    }
    override fun getResult(barSeries: BarSeries): StrategyResult {
        localBarSeries.add(barSeries.last())

        if (barSeries.size < 300) {
            return noSignal()
        }
        if (barSeries.size == 300) {
            initIndicators()
        }
        val diver = diverIndicator.findDivergence(localBarSeries)

        val smaValue = SMA.getValue(closePrice.barSeries.endIndex).doubleValue()
        val filterSmaValue = filterSMA.getValue(closePrice.barSeries.endIndex).doubleValue()
        val ATRValue = ATR.getValue(closePrice.barSeries.endIndex).doubleValue()
        if (smaValue == filterSmaValue) {
            println("$smaValue $filterSmaValue")
        }
        when (diver.type) {
            RsiDivergenceType.BULL -> {
                if (settings.enableSmaFilter && (localBarSeries.last().close > filterSmaValue)) {
                    return noSignal()
                }
                if ((barSeries.last().close < diver.rsiCurrentBar!!.low) || (barSeries.last().close > diver.rsiPrevBar!!.low) || (barSeries.last().close < smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_LONG,
                    arrayOf(Target(barSeries.last().close + (ATRValue * settings.takeProfitAtr) , 100)),
                    diver.rsiCurrentBar.low,
                    createComment(diver)
                )
            }
            RsiDivergenceType.BEAR -> {
                if (settings.enableSmaFilter && (localBarSeries.last().close < filterSmaValue)) {
                    return noSignal()
                }
                if ((barSeries.last().close > diver.rsiCurrentBar!!.high) || (barSeries.last().close < diver.rsiPrevBar!!.high) || (barSeries.last().close > smaValue)) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_SHORT,
                    arrayOf(Target(barSeries.last().close - (ATRValue * settings.takeProfitAtr), 100)),
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