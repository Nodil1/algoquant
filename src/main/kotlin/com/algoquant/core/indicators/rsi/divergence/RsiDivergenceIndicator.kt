package com.algoquant.core.indicators.rsi.divergence

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.indicators.HighLow
import com.algoquant.core.indicators.ta4j.WrappedIndicator
import org.ta4j.core.indicators.RSIIndicator

class RsiDivergenceIndicator(
    private var period: Int,
    var lookLeft: Int,
    private var lookRight: Int
) {

    private val rsiIndicator = WrappedIndicator(RSIIndicator::class.java, period)
    val highLow = HighLow(lookRight, lookLeft)

    fun findDivergence(barSeries: BarSeries): RsiDivergenceResult {
        val rsiValues = mutableListOf<Double>()
        val slicedBars = barSeries.getLast(50)
        rsiIndicator.calculate(slicedBars)
        for (i in slicedBars.getIndices()) {
            rsiValues += rsiIndicator.get(i).doubleValue()
        }

        val highs = highLow.findHigh(rsiValues.toTypedArray())

        val lows = highLow.findLow(rsiValues.toTypedArray())
        val bear = findBear(highs, slicedBars, rsiValues.toTypedArray())
        if (bear.type == RsiDivergenceType.BEAR) {
            return bear
        }
        return findBull(lows, slicedBars, rsiValues.toTypedArray())
    }

    private fun findBear(highs: Array<Int>, barSeries: BarSeries, rsiValues: Array<Double>): RsiDivergenceResult {
        if (rsiValues.last() < 60) {
            //return returnNonDiver()
        }
        if (highs.size < 2) {
            return returnNonDiver()
        }

        val lastIdx = highs.last()
        val prevIdx = highs[highs.size - 2]

        val rsiCond = rsiValues[prevIdx] > rsiValues[lastIdx] // Последний экстремум меньше, чем текущий RSI
        val priceCond =
            barSeries[prevIdx].high < barSeries[lastIdx].high // Цена последнего экстремума больше, чем текущая
        //val ratioCond = calcRatio(rsiValues[prevIdx].rsi, rsiValues[lastIdx].rsi)
        if (rsiCond and priceCond) {
            return RsiDivergenceResult(
                RsiDivergenceType.BEAR,
                rsiValues[prevIdx],
                rsiValues[lastIdx],
                barSeries[prevIdx],
                barSeries[lastIdx]
            )
        }
        return returnNonDiver()
    }

    private fun findBull(lows: Array<Int>, barSeries: BarSeries, rsiValues: Array<Double>): RsiDivergenceResult {
        if (rsiValues.last() > 40) {
            //return returnNonDiver()
        }
        if (lows.size < 2) {
            return returnNonDiver()
        }

        val lastIdx = lows.last()
        val prevIdx = lows[lows.size - 2]

        val rsiCond = rsiValues[prevIdx] < rsiValues[lastIdx] // Последний экстремум меньше, чем текущий RSI
        val priceCond =
            barSeries[prevIdx].low > barSeries[lastIdx].low // Цена последнего экстремума больше, чем текущая
        if (rsiCond and priceCond) {
            return RsiDivergenceResult(
                RsiDivergenceType.BULL,
                rsiValues[prevIdx],
                rsiValues[lastIdx],
                barSeries[prevIdx],
                barSeries[lastIdx]
            )
        }
        return returnNonDiver()
    }

    private fun returnNonDiver(): RsiDivergenceResult {
        return RsiDivergenceResult(
            RsiDivergenceType.NONE,
            0.0,
            0.0
        )
    }
}