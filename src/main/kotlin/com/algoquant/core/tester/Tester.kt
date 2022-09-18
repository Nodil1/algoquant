package com.algoquant.core.tester

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.data.DumpLoader
import com.algoquant.core.managers.deal.TrailStopManager
import com.algoquant.core.statistic.Statistic
import com.algoquant.core.statistic.TestResult
import com.algoquant.core.trader.BasicTrader
import com.algoquant.strategies.RsiDivergenceStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.system.measureTimeMillis

class Tester {
    suspend fun multipleTest(
        array: Array<RsiDivergenceStrategy>,
        nameTest: String,
        period: String,
        symbols: Array<String>
    ) {
        val earn = mutableListOf<Double>()
        val result = TestResult(nameTest)
        array.onEach { strategy ->
            val jobs = mutableListOf<Job>()
            val context = newFixedThreadPoolContext(symbols.size, "co")
            var tmpEarn = 0.0
            var profitCount = 0
            var tmpDeals = 0
            symbols.onEach {
                val job = CoroutineScope(context).launch {
                    val tmp = runBackTest(
                        DumpLoader.loadFromJson(period, it),
                        BasicTrader(
                            RsiDivergenceStrategy(strategy.settings),
                            nameTest,
                            //dealManager = TrailStopManager()
                        )
                    )
                    synchronized(earn){
                        tmpEarn += tmp.metric.totalEarn
                        tmpDeals += tmp.metric.longCount
                        if (tmpEarn > 0){
                            profitCount++
                        }
                    }
                }
                jobs.add(job)
            }
            for (i in jobs.indices) {
                jobs[i].join()
            }
            println("$tmpEarn $tmpDeals ${strategy.settings} $profitCount $")
            earn.add(tmpEarn)
        }
        earn.sort()
        println(earn.joinToString())
    }

    fun createBackTest(array: Array<RsiDivergenceStrategy>, nameTest: String, barSeries: BarSeries): TestResult {
        val earn = mutableListOf<Double>()
        val result = TestResult(nameTest)
        array.onEach {
            val tmp = runBackTest(
                barSeries,
                BasicTrader(
                    it,
                    nameTest,
                    dealManager = TrailStopManager()
                )
            )
            println(tmp.metric)
            println(tmp.printDeals())
            it.settings
            result.add(it.settings, tmp)
        }
        return result
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