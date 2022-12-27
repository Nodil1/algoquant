package com.nodil.algoquant.strategies.extremumCrossMA

import com.nodil.algoquant.core.strategy.StrategySettings

data class ExtremumCrossMASettings(
    val fastMA: Int = 200,
    val slowMA: Int = 500,
    val takeProfitFactor: Int = 1,
) : StrategySettings() {
    companion object {
        fun generate(): Array<ExtremumCrossMASettings> {
            val result = mutableListOf<ExtremumCrossMASettings>()
            for (fastEMA in 50..500 step 10) {
                for (takeProfitFactor in 1..15) {
                    result.add(
                        ExtremumCrossMASettings(
                            fastMA = fastEMA,
                            takeProfitFactor = takeProfitFactor

                        )
                    )
                }
            }
            return result.toTypedArray()

        }

    }
}