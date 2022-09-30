package com.nodil.algoquant.core.strategy

class StrategyResult(
    val action: StrategyAction,
    val targets: Array<com.nodil.algoquant.core.trader.Target>,
    val stopLoss: Double,
    val comment: StrategyComment
)