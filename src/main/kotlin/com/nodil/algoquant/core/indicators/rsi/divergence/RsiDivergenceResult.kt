package com.nodil.algoquant.core.indicators.rsi.divergence

import com.nodil.algoquant.core.bars.Bar


class RsiDivergenceResult(
    val type: RsiDivergenceType,
    val rsiPrev: Double,
    val rsiCurrent: Double,
    val rsiPrevBar: com.nodil.algoquant.core.bars.Bar? = null,
    val rsiCurrentBar: com.nodil.algoquant.core.bars.Bar? = null,
)