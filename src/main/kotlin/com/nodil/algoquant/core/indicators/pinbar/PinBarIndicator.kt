package com.nodil.algoquant.core.indicators.pinbar

import com.nodil.algoquant.core.bars.BarSeries
import kotlin.math.abs

class PinBarIndicator(
    val ratio: Int = 10
) {

    fun findPinBar(barSeries: BarSeries): PinBarResult {
        val lastBar = barSeries.last()
        val body = abs(lastBar.open - lastBar.close)
        val upShadow = abs(lastBar.open - lastBar.high)
        val downShadow = abs(lastBar.open - lastBar.low)
        if (upShadow == 0.0 || downShadow == 0.0 || body == 0.0){
            return PinBarResult.NOTHING
        }
        return if (upShadow / body >= ratio) {
            PinBarResult.BEARISH_PINBAR
        } else if (downShadow / body >= ratio)  {
            PinBarResult.BULLISH_PINBAR
        } else {
            PinBarResult.NOTHING
        }
    }
}