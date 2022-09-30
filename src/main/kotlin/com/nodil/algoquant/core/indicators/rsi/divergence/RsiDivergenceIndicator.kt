package com.nodil.algoquant.core.indicators.rsi.divergence

import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.indicators.HighLow
import com.nodil.algoquant.core.indicators.ta4j.WrappedIndicator
import org.ta4j.core.indicators.RSIIndicator
import kotlin.math.roundToInt

class RsiDivergenceIndicator(
    private var period: Int,
    private var lookLeft: Int,
    private var lookRight: Int
) {
    private val rsiIndicator = WrappedIndicator(RSIIndicator::class.java, period)
    private val highLow = HighLow(lookRight, lookLeft)

    fun findDivergence(barSeries: BarSeries): RsiDivergenceResult {
        val rsiValues = mutableListOf<Double>()
        val slicedBars = barSeries.getLast(50)
        rsiIndicator.calculate(slicedBars)

        for (i in slicedBars.getIndices()) {
            rsiValues += rsiIndicator.get(i).doubleValue().roundToInt().toDouble()
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
           // return returnNonDiver()
        }
        if (highs.size < 2) {
            return returnNonDiver()
        }

        val lastIdx = highs.last()
        for (i in highs.size - 2 .. highs.size - 2) {
            val prevIdx = highs[i]

            val rsiCond = rsiValues[prevIdx] > rsiValues[lastIdx] // Последний экстремум меньше, чем текущий RSI
            val priceCond =
                barSeries[prevIdx].high < barSeries[lastIdx].high // Цена последнего экстремума больше, чем текущая
            if (rsiCond and priceCond) {
                var isCross = false
                for (j in prevIdx until lastIdx) {
                    val it = rsiValues[j]
                    if (it < 60){
                        //isCross = true
                        break
                    }
                }
                if (isCross) continue
                return RsiDivergenceResult(
                    RsiDivergenceType.BEAR,
                    rsiValues[prevIdx],
                    rsiValues[lastIdx],
                    barSeries[prevIdx],
                    barSeries[lastIdx]
                )
            }
        }
        return returnNonDiver()
    }

    private fun findBull(lows: Array<Int>, barSeries: BarSeries, rsiValues: Array<Double>): RsiDivergenceResult {
        if (rsiValues.last() > 40) {
           // return returnNonDiver()
        }
        if (lows.size < 2) {
            return returnNonDiver()
        }

        val lastIdx = lows.last()
        for (i in lows.size - 2 .. lows.size - 2) {
            val prevIdx = lows[i]

            val rsiCond = rsiValues[prevIdx] < rsiValues[lastIdx] // Последний экстремум меньше, чем текущий RSI
            val priceCond =
                barSeries[prevIdx].low > barSeries[lastIdx].low // Цена последнего экстремума больше, чем текущая
            if (rsiCond and priceCond) {
                var isCross = false
                for (j in prevIdx until lastIdx) {
                    val it = rsiValues[j]
                    if (it > 40){
                       // isCross = true
                        break
                    }
                }
                if (isCross) continue
                return RsiDivergenceResult(
                    RsiDivergenceType.BULL,
                    rsiValues[prevIdx],
                    rsiValues[lastIdx],
                    barSeries[prevIdx],
                    barSeries[lastIdx]
                )
            }
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