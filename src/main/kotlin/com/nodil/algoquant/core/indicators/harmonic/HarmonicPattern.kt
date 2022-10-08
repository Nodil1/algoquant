package com.nodil.algoquant.core.indicators.harmonic

import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.indicators.zigzag.ZigZag
import com.nodil.algoquant.core.trader.DealSide
import kotlin.math.round

class HarmonicPattern(
    val depth: Int = 12,
    val deviation: Int = 6,
    val backStep: Int = 3,
    var errorRate: Double = 8.0
) {
    init {
        errorRate /= 100
    }

    private val noPattern = HarmonicPatternResult(
        HarmonicPatternType.NONE,
        mapOf(),
        DealSide.LONG
    )
    private val zigZag = ZigZag(depth, deviation, backStep, 0.0001)


    fun find(barSeries: BarSeries): HarmonicPatternResult {
        if (barSeries.size < 200) {
            return noPattern
        }
        val cuttedSeries = barSeries.getLast(200)
        zigZag.calculate(
            cuttedSeries.size,
            cuttedSeries.getHighPrices().toDoubleArray(),
            cuttedSeries.getLowPrices().toDoubleArray()
        )
        val pivots = zigZag.getZigzagBuffer()

        if (pivots.size < 5) {
            return noPattern
        }
        val abcdResult = findABCD(pivots)
        if (abcdResult != noPattern) {
            return abcdResult
        }
        return noPattern
    }

    private fun findABCD(pivots: DoubleArray): HarmonicPatternResult {
        val pivotsSize = pivots.size

        val a = pivots[pivotsSize - 4]
        val b = pivots[pivotsSize - 3]
        val c = pivots[pivotsSize - 2]
        val d = pivots[pivotsSize - 1]

        val bc = getFibonacciPercent(a, b, c)
        val cd = getFibonacciPercent(b, c, d)

        var bcInRange = false
        var cdInRange = false
        //println("$bcInRange, $cdInRange, $bc, $cd ${pivots.joinToString()}")
        for (i in FourPointPattern.Patterns.ABCD().BC.indices) {
            val fib = FourPointPattern.Patterns.ABCD().BC[i]
            bcInRange = (bc > fib - fib * errorRate) && (bc < fib + fib * errorRate)
            if (bcInRange) break
        }

        for (i in FourPointPattern.Patterns.ABCD().CD.indices) {
            val fib = FourPointPattern.Patterns.ABCD().CD[i]
            cdInRange = (cd > fib - fib * errorRate) && (cd < fib + fib * errorRate)
            if (cdInRange) break
        }

        if (bcInRange && cdInRange) {
            return if (a > b) {
                HarmonicPatternResult(
                    HarmonicPatternType.ABCD,
                    mapOf("BC" to bc, "CD" to cd),
                    DealSide.LONG
                )
            } else {
                HarmonicPatternResult(
                    HarmonicPatternType.ABCD,
                    mapOf("BC" to bc, "CD" to cd),
                    DealSide.SHORT
                )
            }
        }
        return noPattern
    }

    private fun getFibonacciPercent(min: Double, max: Double, pivot: Double): Double {
        val percent = kotlin.math.abs((pivot - max) / (max - min))
        return round(percent * 1000) / 1000
    }


}