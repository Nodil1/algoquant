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
            for (ema in 6..16 step 4) {
                for (rsi in 10..14){
                    for (lookLeft in 4..4) {
                        for (lookRight in 2..3) {
                            for (takeProfit in 1..5 step 1) {
                                arrayOf(true, false).onEach {
                                    result.add(
                                        RsiDivergenceSettings(
                                            rsi,
                                            lookRight,
                                            lookLeft,
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
            }
            return result.toTypedArray()
        }
    }
}