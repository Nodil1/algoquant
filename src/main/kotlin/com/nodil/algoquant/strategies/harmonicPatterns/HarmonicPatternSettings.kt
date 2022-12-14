package com.nodil.algoquant.strategies.harmonicPatterns

import com.nodil.algoquant.core.strategy.StrategySettings

data class HarmonicPatternSettings(
    val depth: Int,
    val deviation: Int,
    val backStep: Int,
    val errorRate: Double,
    val takeProfitAtr: Int
) : StrategySettings() {
    companion object {
        fun generate(): Array<HarmonicPatternSettings> {
            val result = mutableListOf<HarmonicPatternSettings>()
            for (depth in 7..14 step 2) {
                for (deviation in 6..6 step 1) {
                    for (backStep in 4..6 step 1) {
                        for (errorRate in 2.. 14 step 2) {
                            for (takeProfitAtr in 2..10 step 2) {
                                result.add(HarmonicPatternSettings(depth, deviation, backStep, errorRate.toDouble(), takeProfitAtr))
                            }
                        }
                    }
                }
            }
            return result.toTypedArray()
        }
    }
}