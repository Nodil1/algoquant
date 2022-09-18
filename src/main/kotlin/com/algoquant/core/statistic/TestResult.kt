package com.algoquant.core.statistic

import com.algoquant.core.strategy.StrategySettings
import org.ta4j.core.Strategy

class TestResult(
    val name: String
) {

    private val stats = mutableMapOf<StrategySettings, Statistic>()

    fun add(settings: StrategySettings, stat: Statistic){
        stats[settings] = stat
    }
    fun printBest(dealCount: Int = 10){
        val tmp = stats.toList().sortedWith(compareBy({ it.second.metric.totalEarn }, { it.second.metric.totalCount })).reversed()
        tmp.onEachIndexed { index, entry ->
            if (index < 3) {
                println("====$name====\n${entry.second.metric.totalEarn} ${entry.first}\n\n${entry.second.metric}\n==========\n\n")
            }
        }
    }
}