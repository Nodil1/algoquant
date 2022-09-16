package com.algoquant.core.tester

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.indicators.ta4j.WrappedIndicator
import com.algoquant.core.statistic.Statistic
import com.algoquant.core.trader.BasicTrader
import com.algoquant.strategies.RsiDivergenceStrategy
import kotlin.system.measureTimeMillis

class Tester {

    fun createBackTest(array: Array<RsiDivergenceStrategy>, nameTest: String, barSeries: BarSeries) {
        val earn = mutableListOf<Double>()
        array.onEach {
            val tmp = runBackTest(
                barSeries,
                BasicTrader(
                    it,
                    nameTest
                )
            )
            earn.add(tmp.metric.totalEarn)
            println(tmp.printDeals())
            println(tmp.metric)
        }
        earn.sort()
        println(earn.joinToString())
    }

    private fun runBackTest(barSeries: BarSeries, bot: BasicTrader): Statistic {
        val time = measureTimeMillis {
            barSeries.getIterator().forEach {
                bot.update(it)
            }
        }
        return bot.statistic
    }
}