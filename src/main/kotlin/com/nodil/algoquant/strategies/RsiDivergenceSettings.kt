package com.nodil.algoquant.strategies

import com.nodil.algoquant.core.strategy.StrategySettings

data class RsiDivergenceSettings(
    val rsiPeriod: Int,
    val lookRight: Int,
    val lookLeft: Int,
    val takeProfitAtr: Int,
    val emaPeriod: Int,
    val enableSmaFilter: Boolean
) : StrategySettings() {
    companion object {
        fun generate(): Array<RsiDivergenceSettings> {
            val result = mutableListOf<RsiDivergenceSettings>()
            for (ema in 2..26 step 4) {
                for (rsi in 10..13) {
                    for (look in 3..4) {
                        for (takeProfit in 1..12 step 3) {
                            arrayOf(true, false).onEach {
                                result.add(
                                    RsiDivergenceSettings(
                                        rsi,
                                        look,
                                        look,
                                        takeProfit,
                                        ema,
                                        it
                                    )
                                )
                            }
                        }
                    }
                }
            }
            return result.toTypedArray()
        }
    }
}