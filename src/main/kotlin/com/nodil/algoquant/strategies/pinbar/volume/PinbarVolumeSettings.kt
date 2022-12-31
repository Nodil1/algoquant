package com.nodil.algoquant.strategies.pinbar.volume

import com.nodil.algoquant.core.strategy.StrategySettings
import com.nodil.algoquant.strategies.pinbar.PinbarSettings

data class PinbarVolumeSettings(
    val ratio: Int,
    val bbSize: Int,
    val bbDev: Double,
    val takeProfitPercent: Double,
    val volumeFactor: Int
) : StrategySettings() {
    companion object {
        fun generate(): Array<PinbarVolumeSettings> {
            val result = mutableListOf<PinbarVolumeSettings>()
            for (i in 5..20 step 5) {
                for (_bbSize in 9..26 step 4) {
                    for (_bbDev in 30..45 step 5) {
                        for (_takeProfit in 1..15 step 3 )
                            for (_vf in 1..4)
                            result.add(
                                PinbarVolumeSettings(
                                    i,
                                    _bbSize,
                                    _bbDev.toDouble() / 10,
                                    _takeProfit.toDouble(),
                                    _vf
                                )
                            )
                    }
                }
            }
            return result.toTypedArray()
        }
    }

    fun toPinbarSettings(): PinbarSettings {
        return PinbarSettings(ratio, bbSize, bbDev, takeProfitPercent)
    }
}
