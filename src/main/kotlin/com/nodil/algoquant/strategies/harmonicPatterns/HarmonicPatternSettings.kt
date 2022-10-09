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
            for (depth in 9..12 step 1) {
                for (deviation in 6..7 step 3) {
                    for (backStep in 6..6 step 1) {
                        for (errorRate in 4..14 step 2) {
                            for (takeProfitAtr in 2..18 step 2) {
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