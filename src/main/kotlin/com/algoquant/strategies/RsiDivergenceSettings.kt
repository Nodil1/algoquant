package com.algoquant.strategies

import com.algoquant.core.strategy.StrategySettings

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
            for (ema in 3..6 step 2) {
                for (rsi in 10..14) {
                    for (look in 2..4) {
                        for (takeProfit in 4..20 step 3) {
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