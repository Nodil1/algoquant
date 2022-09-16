package com.algoquant.core.strategy

import com.algoquant.core.bars.Bar

class Signal(
    val signalBar: Bar,
    val waitSignal: Int,
    val strategyResult: StrategyResult
) {
    val currentWaitSignal: Int = 0

}