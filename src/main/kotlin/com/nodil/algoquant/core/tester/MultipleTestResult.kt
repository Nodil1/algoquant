package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.statistic.Metric
import com.nodil.algoquant.core.utils.roundToTwoDecimal
import com.nodil.algoquant.core.utils.statistics.Median
import com.nodil.algoquant.core.utils.statistics.ProfitFactor
import com.nodil.algoquant.core.utils.statistics.RecoveryFactor
import com.nodil.algoquant.core.utils.statistics.SharpeRatio
import kotlin.math.abs

class MultipleTestResult(
    val startMoney: Double = 100.0
) {
    private val pairs = mutableMapOf<String, BackTestResult>()


    val summaryEarn: Double
        get() {
            var sum = 0.0
            pairs.onEach {
                sum += it.value.statistic.metric.totalEarn
            }
            return sum
        }

    val profitPairPercent: Int
        get() {
            var profitCount = 0
            pairs.onEach {
                if (it.value.statistic.summaryEarn > 0) {
                    profitCount++
                }
            }
            return ((profitCount.toDouble() / (pairs.size.toDouble())) * 100).toInt()
        }

    val metric: Metric
        get() {
            val tmpMetric = Metric()
            pairs.onEach {
                tmpMetric + it.value.statistic.metric
            }
            return tmpMetric
        }

    val testRecord: TestRecord
        get() {
            val tmpRecord = TestRecord()
            pairs.onEach {
                tmpRecord + it.value.record
            }
            return tmpRecord
        }

    val sharp: Double
        get() {
            if (summaryEarn < 0){
                return -1.0
            }
            return SharpeRatio.calc(startMoney * 0.03, summaryEarn, testRecord.getProfits())
        }

    val profitFactor: Double
        get() {
            if (summaryEarn < 0){
                return -1.0
            }
            return ProfitFactor.calc(metric.totalProfit, abs(metric.totalLoss))
        }

    val recoveryFactor: Double
        get() {
            if (summaryEarn < 0){
                return -1.0
            }
            return RecoveryFactor.calc(summaryEarn, testRecord.dropDown)
        }

    fun add(pairName: String, testResult: BackTestResult) {
        pairs[pairName] = testResult
    }
    fun printDeals(){
        pairs.onEach {
            println(it.key)
            it.value.statistic.printDeals()
        }
    }

    override fun toString(): String {
        val metric = this.metric
        val record = testRecord
        var pairHandle = ""
        pairs.onEach {
            if (it.value.statistic.summaryEarn > 0)
                pairHandle += "\"${it.key}\","
        }
        return "Earn ${summaryEarn.toInt()}$ Profit $profitPairPercent% MaxProfit ${record.maxProfit.toInt()}$ DropDown ${record.dropDown.toInt()}$ Long ${metric.longProfit.toInt()}$ Short ${metric.shortProfit.toInt()}\$ Ratio ${metric.profitLossRatio} Size ${metric.profitLossSize} Deals ${metric.totalCount}\n" +
                "Statistic Analysis: Sharpe Ratio: ${sharp.roundToTwoDecimal()} PF: ${profitFactor.roundToTwoDecimal()} RF: ${recoveryFactor.roundToTwoDecimal()}\n" +
                "$pairHandle"
    }


}