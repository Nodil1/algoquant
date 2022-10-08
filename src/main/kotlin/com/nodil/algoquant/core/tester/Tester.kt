package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.data.DumpLoader
import com.nodil.algoquant.core.managers.deal.TrailStopManager
import com.nodil.algoquant.core.managers.money.SideBasedManager
import com.nodil.algoquant.core.strategy.Strategy
import com.nodil.algoquant.core.strategy.StrategySettings
import com.nodil.algoquant.core.trader.BasicTrader
import com.nodil.algoquant.strategies.RsiDivergenceStrategy
import com.nodil.algoquant.strategies.extremumCrossMA.ExtremumCrossMASettings
import com.nodil.algoquant.strategies.extremumCrossMA.ExtremumCrossMAStrategy
import kotlinx.coroutines.*
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
                            dealManager = TrailStopManager(3),
                            moneyManager = SideBasedManager(100.0)
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
        val best = multipleTestContainer.getBestEarn()
        best.testRecord.toCsv("biba")
        multipleTestContainer.getBestSharp().testRecord.toCsv("sharp")


    }

    fun <StrategySetting : StrategySettings, StrategyType : Strategy> multipleTestSingleThread(
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
        settings.onEach { setting ->
            val jobs = mutableListOf<Job>()
            val multipleTestResult = MultipleTestResult()
            symbolsDump.onEach {
                val tmp = runBackTest(
                    it.value,
                    BasicTrader(
                        strategyClass.constructors[0].newInstance(setting) as Strategy,
                        nameTest,
                        // dealManager = TrailStopManager(2),
                        moneyManager = SideBasedManager(100.0)
                    )
                )
                synchronized(multipleTestResult) {
                    multipleTestResult.add(it.key, tmp)
                }
            }

            j++
            println("$j/${settings.size} ${setting} $multipleTestResult")
            //println(multipleTestResult.metric)
            multipleTestContainer.add(multipleTestResult)
        }
        val best = multipleTestContainer.getBestEarn()
        best.testRecord.toCsv("biba")
        multipleTestContainer.getMaxWinRate().testRecord.toCsv("winRate")
        multipleTestContainer.getMinDropDown().testRecord.toCsv("minDropDown")

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
        return BackTestResult(record, bot.statistic)
    }

}