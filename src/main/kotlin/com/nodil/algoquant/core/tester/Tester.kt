package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.data.DumpLoader
import com.nodil.algoquant.core.managers.deal.TrailStopManager
import com.nodil.algoquant.core.statistic.Metric
import com.nodil.algoquant.core.trader.BasicTrader
import com.nodil.algoquant.strategies.RsiDivergenceStrategy
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
        printMetric: Boolean = false,
    ) {
        val earns = mutableListOf<Double>()
        array.onEach { strategy ->
            val jobs = mutableListOf<Job>()
            val context = newFixedThreadPoolContext(2, "co")
            var tmpEarn = 0.0
            var tmpProfitPairs = 0
            val tmpMetric = Metric()
            val tmpRecord = TestRecord()
            val tmpProfitPair = arrayListOf<String>()
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
                    synchronized(earns) {
                        if (tmp.statistic.summaryEarn > 0){
                            tmpProfitPairs++
                            tmpProfitPair.add(it)
                        }
                        tmpEarn += tmp.statistic.summaryEarn
                        tmpMetric + tmp.statistic.metric
                        tmpRecord + tmp.record
                        earns.add(tmpEarn)
                    }
                }
                jobs.add(job)
            }
            for (i in jobs.indices) {
                jobs[i].join()
            }
            earns.add(tmpEarn)
            println("${strategy.settings} Earn $tmpEarn Profit $tmpProfitPairs MaxProfit ${tmpRecord.maxProfit} DropDown ${tmpRecord.dropDown} Long ${tmpMetric.longProfit} Short ${tmpMetric.shortProfit} ")
            var pairHandle = ""
            tmpProfitPair.onEach {
                pairHandle += "\"$it\","
            }
            println(pairHandle)
            if (printMetric) println("Metric: \n$tmpMetric")
        }
        earns.sort()
        println(earns.joinToString())
    }

    fun createBackTest(array: Array<RsiDivergenceStrategy>, nameTest: String, barSeries: com.nodil.algoquant.core.bars.BarSeries): TestResult {
        val earn = mutableListOf<Double>()
        val result = TestResult(nameTest)
        array.onEach {
            val tmp = runBackTest(
                barSeries,
                BasicTrader(
                    it,
                    nameTest,
                    dealManager = TrailStopManager(6)
                )
            )
            tmp.settings = it.settings
            result.add(tmp)
        }
        return result
    }

    private fun runBackTest(barSeries: com.nodil.algoquant.core.bars.BarSeries, bot: BasicTrader): BackTestResult {
        val record = TestRecord()
        val time = measureTimeMillis {
            barSeries.getIterator().forEach {
                record.add(it, bot.statistic.summaryEarn)
                bot.update(it)
            }
        }
        return BackTestResult(record, bot.statistic)
    }

}