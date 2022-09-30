package com.nodil.algoquant.core.strategy

import com.nodil.algoquant.core.bars.Bar

class Signal(
    val signalBar: com.nodil.algoquant.core.bars.Bar,
    val waitSignal: Int,
    val strategyResult: StrategyResult
) {
    val currentWaitSignal: Int = 0

}