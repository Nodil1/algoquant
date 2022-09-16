package com.algoquant.core.strategy

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.trader.Target

abstract class Strategy {
    private val signal: Signal? = null
    var allowTrading = false
    val strategyName: String
        get() = this.javaClass.simpleName

    abstract fun getResult(barSeries: BarSeries): StrategyResult
    protected fun noSignal(): StrategyResult {
        return StrategyResult(
            StrategyAction.IDLE,
            arrayOf(),
            0.0,
            StrategyComment())
    }
}