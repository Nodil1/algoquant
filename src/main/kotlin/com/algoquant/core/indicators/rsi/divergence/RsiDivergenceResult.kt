package com.algoquant.core.indicators.rsi.divergence

import com.algoquant.core.bars.Bar


class RsiDivergenceResult(
    val type: RsiDivergenceType,
    val rsiPrev: Double,
    val rsiCurrent: Double,
    val rsiPrevBar: Bar? = null,
    val rsiCurrentBar: Bar? = null,
)