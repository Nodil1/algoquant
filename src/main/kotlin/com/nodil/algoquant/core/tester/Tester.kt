package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.data.DumpLoader
import com.nodil.algoquant.core.managers.deal.TrailStopManager
import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategySettings
import com.nodil.algoquant.core.trader.BasicTrader
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
        minEarn: Int

    ) {
        var j = 0
        val multipleTestContainer = MultipleTestContainer()
        val symbolsDump = DumpLoader.loadMultiple(symbols, period)
        println("TOTAL ${settings.size}")
        System.gc()
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
                        ),
                        setting
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
            if (j % 100 == 0) {
                println(j)
                System.gc()
            }
            if (multipleTestResult.summaryEarn >= minEarn) {
                println("$j/${settings.size} Sharp ${multipleTestResult.sharp} ${setting} $multipleTestResult")
                multipleTestContainer.add(multipleTestResult)
            }

        }
        println("BEST EARN")
        multipleTestContainer.getBestEarn().testRecord.toCsv("biba")
        println("BEST SHARP")
        multipleTestContainer.getBestSharp().testRecord.toCsv("sharp")
        println("BEST SLOPE")
        multipleTestContainer.getBestSlope().testRecord.toCsv("slope")

        println("BEST PROFIT FACTOR")
        multipleTestContainer.getBestProfitFactor().testRecord.toCsv("pf")
        multipleTestContainer.getBestProfitFactor().printDeals()

        println("BEST RECOVERY FACTOR")
        multipleTestContainer.getBestRecoveryFactor().testRecord.toCsv("recovery")

    }


    private fun runBackTest(barSeries: com.nodil.algoquant.core.bars.BarSeries, bot: BasicTrader, settings: StrategySettings): BackTestResult {
        val record = TestRecord()
        record.prices = barSeries
        val time = measureTimeMillis {
            barSeries.getIterator().forEach {
                record.add(bot.statistic.summaryEarn)
                bot.update(it)
            }
        }
        bot.stop()
        return BackTestResult(record, bot.statistic, settings)
    }

}