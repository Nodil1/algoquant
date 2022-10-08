package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.statistic.Statistic
import com.nodil.algoquant.core.strategy.StrategySettings

class BackTestResult(
    val record: TestRecord,
    val statistic: Statistic,
    val startMoney: Double = 100.0
) {
    lateinit var settings: StrategySettings
    val sharpRatio: Double
    get() {
        val nonRisk = 3.0
        val profitPercent = (statistic.summaryEarn / startMoney) * 100
        val dropDown = record.dropDown
        if (dropDown == 0.0) return 0.0
        return (profitPercent - nonRisk) / dropDown
    }
}