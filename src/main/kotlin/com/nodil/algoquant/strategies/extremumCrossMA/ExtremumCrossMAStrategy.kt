package com.nodil.algoquant.strategies.extremumCrossMA

import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategyAction
import com.nodil.algoquant.core.strategy.StrategyComment
import com.nodil.algoquant.core.strategy.StrategyResult
import com.nodil.algoquant.core.trader.Target
import com.nodil.algoquant.core.utils.last
import org.ta4j.core.indicators.ATRIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.ZLEMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator


class ExtremumCrossMAStrategy(
    override val settings: ExtremumCrossMASettings
) : Strategy(settings) {
    private val closePriceIndicator by lazy { ClosePriceIndicator(barSeries) }

    private val fastMA by lazy { ZLEMAIndicator(closePriceIndicator, settings.fastMA) }
    private val slowMA by lazy { SMAIndicator(closePriceIndicator, settings.slowMA) }
    private val rsi by lazy { RSIIndicator(closePriceIndicator, 14) }
    private val atr by lazy { ATRIndicator(barSeries, 14) }


    override fun getResult(): StrategyResult {
        if (barSeries.size < 501) {
            return noSignal()
        }
        val lastBar = barSeries.last()
        val lastFastMAValues = fastMA.last().doubleValue()
        val atrValue = atr.last().doubleValue()
        val volumeRule = lastBar.volume > barSeries[barSeries.size - 2].volume
        val maRange = lastFastMAValues * 0.999..lastFastMAValues * 1.001
        val rsiValue = rsi.last().doubleValue()
        //println("$lastFastMAValues ${maRange.toString()}")
        if (lastBar.high in maRange && lastBar.close < lastFastMAValues && lastBar.open < lastFastMAValues && rsiValue > 70) {
            return StrategyResult(
                StrategyAction.OPEN_SHORT,
                arrayOf(
                    Target(lastBar.close - (atrValue * 2 * settings.takeProfitFactor), 100),
                ),
                lastBar.high,
                StrategyComment()
            ).inverse()
        } else if (lastBar.low in maRange && lastBar.close > lastFastMAValues && lastBar.open > lastFastMAValues && rsiValue < 30) {

            return StrategyResult(
                StrategyAction.OPEN_LONG,
                arrayOf(
                    Target(lastBar.close + (atrValue * 1 * settings.takeProfitFactor), 100),
                ),
                lastBar.low,
                StrategyComment()
            ).inverse()
        }
        return noSignal()
    }

    companion object {
        fun generate(): Array<ExtremumCrossMAStrategy> {
            val result = mutableListOf<ExtremumCrossMAStrategy>()
            ExtremumCrossMASettings.generate().onEach {
                result.add(ExtremumCrossMAStrategy(it))
            }
            return result.toTypedArray()
        }
    }
}