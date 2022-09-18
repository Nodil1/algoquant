package com.algoquant.strategies

import com.algoquant.core.strategy.StrategySettings

data class RsiDivergenceSettings(
    val rsiPeriod: Int,
    val lookRight: Int,
    val lookLeft: Int,
    val takeProfit: Double,
    val emaPeriod: Int
) : StrategySettings() {
    companion object {
        fun generate(): Array<RsiDivergenceSettings> {
            val result = mutableListOf<RsiDivergenceSettings>()
            for (ema in 8..20 step 4) {
                for (rsi in 9..12) {
                    for (look in 3..4) {
                        for (takeProfit in 4..10) {
                            result.add(
                                RsiDivergenceSettings(
                                    rsi,
                                    look,
                                    look,
                                    takeProfit / 100.0,
                                    ema
                                )
                            )
                        }
                    }
                }
            }
            return result.toTypedArray()
        }
    }
}