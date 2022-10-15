package com.nodil.algoquant.strategies.harmonicPatterns

import com.nodil.algoquant.core.indicators.harmonic.HarmonicPattern
import com.nodil.algoquant.core.indicators.harmonic.HarmonicPatternType
import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceIndicator
import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceResult
import com.nodil.algoquant.core.indicators.rsi.divergence.RsiDivergenceType
import com.nodil.algoquant.core.strategy.*
import com.nodil.algoquant.core.trader.DealSide
import com.nodil.algoquant.core.trader.Target
import com.nodil.algoquant.core.utils.last
import org.ta4j.core.indicators.*
import org.ta4j.core.indicators.helpers.ClosePriceIndicator

class HarmonicStrategy(
    override val settings: HarmonicPatternSettings
) : Strategy(settings) {
    private val harmonicPatternIndicator by lazy { HarmonicPattern(settings.depth, settings.deviation, settings.backStep, settings.errorRate)}
    private val ATR by lazy { ATRIndicator(barSeries, 14) }
    private val closePriceIndicator by lazy { ClosePriceIndicator(barSeries) }

    private val RSI by lazy { RSIIndicator(closePriceIndicator, 14) }


    override fun getResult(): StrategyResult {
        if (barSeries.size < 501) {
            return noSignal()
        }
        val resultPattern = harmonicPatternIndicator.find(barSeries)
        if (resultPattern.type == HarmonicPatternType.NONE){
            return noSignal()
        }
        when (resultPattern.side) {

            DealSide.LONG -> {
                if(RSI.last().doubleValue() < 70) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_LONG,
                    arrayOf(Target(barSeries.last().close + (ATR.last().doubleValue() * settings.takeProfitAtr) , 100)),
                    barSeries.last().close - ATR.last().doubleValue(),
                    createComment()
                )
            }
            DealSide.SHORT -> {
                if(RSI.last().doubleValue() > 70) {
                    return noSignal()
                }
                return StrategyResult(
                    StrategyAction.OPEN_SHORT,
                    arrayOf(Target(barSeries.last().close - (ATR.last().doubleValue() * settings.takeProfitAtr) , 100)),
                    barSeries.last().close + ATR.last().doubleValue(),
                    createComment()
                )
            }
            else -> return noSignal()

        }
    }
    private fun createComment(): StrategyComment {
        val comment = StrategyComment()

        return comment
    }

    companion object {
        fun generate(): Array<HarmonicStrategy> {
            val result = mutableListOf<HarmonicStrategy>()
            HarmonicPatternSettings.generate().onEach {
                result.add(HarmonicStrategy(it))
            }
            return result.toTypedArray()
        }
    }
}