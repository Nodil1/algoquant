package com.nodil.algoquant.core.indicators.harmonic

import com.nodil.algoquant.core.trader.DealSide

class HarmonicPatternResult(
    val type: HarmonicPatternType,
    val factors: Map<String, Double>,
    val side: DealSide
)