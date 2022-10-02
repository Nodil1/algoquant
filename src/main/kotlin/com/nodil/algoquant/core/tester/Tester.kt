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
import org.ta4j.core.BarSeries
import kotlin.system.measureTimeMillis

class Tester {
    suspend fun multipleTest(
        array: Array<RsiDivergenceStrategy>,
        nameTest: String,
        period: String,
        symbols: Array<String>,
        printMetric: Boolean = false,
    ) {
        var j = 0
        val multipleTestContainer = MultipleTestContainer()
        val symbolsDump = DumpLoader.loadMultiple(symbols, period)
        array.onEach { strategy ->
            val jobs = mutableListOf<Job>()
            val context = newFixedThreadPoolContext(12, "co")
            val multipleTestResult = MultipleTestResult()
            symbolsDump.onEach {
                val job = CoroutineScope(context).launch {
                    val tmp = runBackTest(
                        it.value,
                        BasicTrader(
                            RsiDivergenceStrategy(strategy.settings),
                            nameTest,
                            dealManager = TrailStopManager(2),
                        )
                    )
                    synchronized(multipleTestResult) {
                        multipleTestResult.add(it.key, tmp)
                    }
                }
                jobs.add(job)
            }
            for (i in jobs.indices) {
                jobs[i].join()
            }
            j++
            println("$j/${array.size} ${strategy.settings} $multipleTestResult")
            multipleTestContainer.add(multipleTestResult)
        }
        val best = multipleTestContainer.getBestEarn()
        best.testRecord.toCsv("biba")
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