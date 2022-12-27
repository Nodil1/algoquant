package com.nodil.algoquant.core.strategy

class Signal(
    val signalBar: com.nodil.algoquant.core.bars.Bar,
    val waitSignal: Int,
    val strategyResult: StrategyResult
) {
    val currentWaitSignal: Int = 0

}