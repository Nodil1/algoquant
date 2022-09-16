package com.algoquant.strategies

class RsiDivergenceSettings(
    val rsiPeriod: Int,
    val lookRight: Int,
    val lookLeft: Int,
    val takeProfit: Double,
    val emaPeriod: Int
) {
    companion object {
        fun generate(): Array<RsiDivergenceSettings> {
            val result = mutableListOf<RsiDivergenceSettings>()
            for (ema in 6..20 step 4) {
                for (rsi in 7..19) {
                    for (look in 2..4) {
                        for (takeProfit in 1..2) {
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