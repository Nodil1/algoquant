package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.data.DumpLoader
import com.nodil.algoquant.core.managers.deal.TrailStopManager
import com.nodil.algoquant.core.managers.money.SideBasedManager
import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategySettings
import com.nodil.algoquant.core.trader.BasicTrader
import com.nodil.algoquant.strategies.extremumCrossMA.ExtremumCrossMAStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class Tester {
    suspend fun <StrategySetting : StrategySettings, StrategyType : Strategy> multipleTest(
        settings: Array<StrategySetting>,
        strategyClass: Class<StrategyType>,
        nameTest: String,
        period: String,
        symbols: Array<String>,
        printMetric: Boolean = false,

        ) {
        var j = 0
        val multipleTestContainer = MultipleTestContainer()
        val symbolsDump = DumpLoader.loadMultiple(symbols, period)
        readLine()
        settings.onEach { setting ->
            val jobs = mutableListOf<Job>()
            val multipleTestResult = MultipleTestResult()
            symbolsDump.onEach {
                val job = CoroutineScope(Dispatchers.Default).launch {
                    val tmp = runBackTest(
                        it.value,
                        BasicTrader(
                            strategyClass.constructors[0].newInstance(setting) as Strategy,
                            nameTest,
                            //dealManager = TrailStopManager(3),
                            //moneyManager = SideBasedManager(100.0)
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
            println("$j/${settings.size} Sharp ${multipleTestResult.sharp} ${setting} $multipleTestResult")
            multipleTestContainer.add(multipleTestResult)
        }
        println("BEST EARN")
        multipleTestContainer.getBestEarn().testRecord.toCsv("biba")
        println("BEST SHARP")
        multipleTestContainer.getBestSharp().testRecord.toCsv("sharp")
        println("BEST PROFIT FACTOR")
        multipleTestContainer.getBestProfitFactor().testRecord.toCsv("pf")
        println("BEST RECOVERY FACTOR")
        multipleTestContainer.getBestRecoveryFactor().testRecord.toCsv("recovery")
        println("BEST MEAN")
        multipleTestContainer.getBestMean().testRecord.toCsv("mean")


    }


    fun createBackTest(array: Array<ExtremumCrossMAStrategy>, nameTest: String, barSeries: BarSeries): TestResult {
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
            tmp.settings = it.settings
            result.add(tmp)
        }
        return result
    }

    private fun runBackTest(barSeries: com.nodil.algoquant.core.bars.BarSeries, bot: BasicTrader): BackTestResult {
        val record = TestRecord()
        record.prices = barSeries
        val time = measureTimeMillis {
            barSeries.getIterator().forEach {
                record.add(bot.statistic.summaryEarn)
                bot.update(it)
            }
        }
        bot.stop()
        return BackTestResult(record, bot.statistic)
    }

}