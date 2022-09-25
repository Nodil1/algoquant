package com.algoquant.core.tester

import com.algoquant.core.bars.BarSeries
import com.algoquant.core.data.DumpLoader
import com.algoquant.core.managers.deal.TrailStopManager
import com.algoquant.core.statistic.Metric
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
        symbols: Array<String>,
        dealManagers: Array<TrailStopManager>
    ) {
        val earn = mutableListOf<Double>()

        val result = TestResult(nameTest)
        array.onEach { strategy ->
            val jobs = mutableListOf<Job>()
            val context = newFixedThreadPoolContext(2, "co")
            var tmpEarn = 0.0
            var profitCount = 0
            var tmpDeals = 0
            val totalMetric = Metric()

            symbols.onEach {
                val job = CoroutineScope(context).launch {

                    val tmp = runBackTest(
                        DumpLoader.loadFromJson(period, it),
                        BasicTrader(
                            RsiDivergenceStrategy(strategy.settings),
                            nameTest,
                            dealManager = TrailStopManager(2),
                        )
                    )
                    synchronized(earn) {
                        totalMetric + tmp.metric
                        tmpEarn += tmp.metric.totalEarn
                        tmpDeals += tmp.metric.totalCount
                        if (tmpEarn > 0) {
                            profitCount++
                        }
                    }
                }
                jobs.add(job)
            }
            for (i in jobs.indices) {
                jobs[i].join()
            }
            println("$tmpEarn $tmpDeals ${strategy.settings} $profitCount Ratio ${totalMetric.profitLossRatio} Size ${totalMetric.profitLossSize} Long ${totalMetric.totalLong} Short ${totalMetric.totalShort}")
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
                    dealManager = TrailStopManager(2)
                )
            )
            //println(tmp.metric)
            //println(tmp.printDeals())
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