package com.nodil.algoquant.core.strategy

import com.nodil.algoquant.core.trader.Target

class StrategyResult(
    var action: StrategyAction,
    var targets: Array<com.nodil.algoquant.core.trader.Target>,
    var stopLoss: Double,
    var comment: StrategyComment
) {
    fun inverse(): StrategyResult {
        action = if (action == StrategyAction.OPEN_LONG){
            StrategyAction.OPEN_SHORT
        } else {
            StrategyAction.OPEN_LONG
        }
        val stopLossPrice = stopLoss
        stopLoss = targets.last().triggerPrice
        targets = arrayOf(com.nodil.algoquant.core.trader.Target(stopLossPrice, 100))
        return this
    }
}