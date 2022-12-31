package com.nodil.algoquant.strategies.pinbar

import com.nodil.algoquant.core.indicators.pinbar.PinBarIndicator
import com.nodil.algoquant.core.indicators.pinbar.PinBarResult
import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategyAction
import com.nodil.algoquant.core.strategy.StrategyComment
import com.nodil.algoquant.core.strategy.StrategyResult
import com.nodil.algoquant.core.trader.Target
import com.nodil.algoquant.core.utils.last
import org.ta4j.core.indicators.ATRIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandFacade
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import kotlin.math.abs

open class PinbarStrategy(
    override val settings: PinbarSettings
) : Strategy(settings) {
    private val pinBarIndicator = PinBarIndicator(settings.ratio)
    private val closePriceIndicator by lazy { ClosePriceIndicator(barSeries) }

    private val bb by lazy { BollingerBandFacade(closePriceIndicator, settings.bbSize, settings.bbDev) }
    private val ATR by lazy { ATRIndicator(barSeries, 14) }

    override fun getResult(): StrategyResult {
        if (barSeries.size < 100) {
            return noSignal()
        }
        return when (pinBarIndicator.findPinBar(barSeries)) {
            PinBarResult.BULLISH_PINBAR -> {
                if (barSeries.last().low > bb.lower().last().doubleValue()){
                    return noSignal()
                }
                StrategyResult(
                    StrategyAction.OPEN_LONG,
                    arrayOf(
                        Target(barSeries.last().close + (ATR.last().doubleValue() * settings.takeProfitPercent), 50),
                        Target(barSeries.last().close + (ATR.last().doubleValue() * settings.takeProfitPercent * 2), 50)
                    ),
                    barSeries.last().low,
                    StrategyComment()
                )
            }
            PinBarResult.BEARISH_PINBAR -> {
                if (barSeries.last().high < bb.upper().last().doubleValue()){
                    return noSignal()
                }
                StrategyResult(
                    StrategyAction.OPEN_SHORT,
                    arrayOf(
                        Target(barSeries.last().close - (ATR.last().doubleValue() * settings.takeProfitPercent), 50),
                        Target(barSeries.last().close - (ATR.last().doubleValue() * settings.takeProfitPercent * 2), 50)
                    ),
                    barSeries.last().high,
                    StrategyComment()
                )
            }
            else -> {
                noSignal()
            }
        }

    }

    companion object {
        fun generate(): Array<PinbarStrategy> {
            val result = mutableListOf<PinbarStrategy>()
            PinbarSettings.generate().onEach {
                result.add(PinbarStrategy(it))
            }
            return result.toTypedArray()
        }
    }
}