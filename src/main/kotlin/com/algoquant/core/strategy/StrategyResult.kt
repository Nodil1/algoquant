package com.algoquant.core.strategy

class StrategyResult(
    val action: StrategyAction,
    val targets: Array<com.algoquant.core.trader.Target>,
    val stopLoss: Double,
    val comment: StrategyComment
)