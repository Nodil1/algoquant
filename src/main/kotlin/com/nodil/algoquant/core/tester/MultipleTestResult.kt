package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.statistic.Metric

class MultipleTestResult{
    private val pairs = mutableMapOf<String, BackTestResult>()

    val summaryEarn : Double
    get() {
        var sum = 0.0
        pairs.onEach {
            sum += it.value.statistic.summaryEarn
        }
        return sum
    }
    val profitPairPercent : Int
    get() {
        var profitCount = 0
        pairs.onEach {
            if (it.value.statistic.summaryEarn > 0){
                profitCount++
            }
        }
        return ((profitCount.toDouble() / (pairs.size.toDouble())) * 100).toInt()
    }
    val metric : Metric
    get() {
        val tmpMetric = Metric()
        pairs.onEach {
            tmpMetric + it.value.statistic.metric
        }
        return tmpMetric
    }
    val testRecord : TestRecord
    get() {
        val tmpRecord = TestRecord()
        pairs.onEach {
            tmpRecord + it.value.record
        }
        return tmpRecord
    }

    fun add(pairName: String, testResult: BackTestResult){
        pairs[pairName] = testResult
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
                "$pairHandle"
    }
}