package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.statistic.Statistic
import com.nodil.algoquant.core.strategy.StrategySettings

class BackTestResult(
    val record: TestRecord,
    val statistic: Statistic,

    ) {
    lateinit var settings: StrategySettings
}