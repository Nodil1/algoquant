package com.nodil.algoquant.strategies.pinbar.volume

import com.nodil.algoquant.core.strategy.StrategyAction
import com.nodil.algoquant.core.strategy.StrategyResult
import com.nodil.algoquant.core.utils.last
import com.nodil.algoquant.strategies.pinbar.PinbarStrategy
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.indicators.helpers.VolumeIndicator

class PinbarVolumeStrategy(
    private val pinbarVolumeSettings: PinbarVolumeSettings
): PinbarStrategy(pinbarVolumeSettings.toPinbarSettings()) {
    private val volumeIndicator by lazy { VolumeIndicator(barSeries) }
    private val MA by lazy { SMAIndicator(volumeIndicator, 14) }

    override fun getResult(): StrategyResult {
        val result = super.getResult()
        return if (result.action != StrategyAction.IDLE){
            if (barSeries.last().volume / MA.last().doubleValue() > pinbarVolumeSettings.volumeFactor ){
                result
            } else {
                noSignal()
            }
        } else {
            result
        }
    }
}