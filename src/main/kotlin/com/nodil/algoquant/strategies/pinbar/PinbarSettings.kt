package com.nodil.algoquant.strategies.pinbar

import com.nodil.algoquant.core.strategy.StrategySettings

data class PinbarSettings(
    val ratio: Int,
    val bbSize: Int,
    val bbDev: Double,
    val takeProfitPercent: Double
) : StrategySettings() {
    companion object {
        fun generate(): Array<PinbarSettings> {
            val result = mutableListOf<PinbarSettings>()
            for (i in 5..50 step 10) {
                for (_bbSize in 9..26 step 4) {
                    for (_bbDev in 5..40 step 5) {
                        for (_takeProfit in 1..10)
                            result.add(
                                PinbarSettings(
                                    i,
                                    _bbSize,
                                    _bbDev.toDouble() / 10,
                                    _takeProfit.toDouble() / 200
                                )
                            )
                    }
                }
            }
            return result.toTypedArray()
        }
    }
}
