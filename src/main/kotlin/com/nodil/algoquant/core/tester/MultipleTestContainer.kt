package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.utils.toSequence
import org.nield.kotlinstatistics.simpleRegression

class MultipleTestContainer {
    private val tests = mutableListOf<MultipleTestResult>()
    fun add(multipleTestResult: MultipleTestResult) {
        tests.add(multipleTestResult)
    }

    fun getBestEarn(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.summaryEarn })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }

    fun getMinDropDown(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.testRecord.dropDown }).reversed()
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }

    fun getMaxWinRate(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.metric.profitLossRatio })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }

    fun getBestSharp(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.sharp })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }

    fun getBestRecoveryFactor(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.recoveryFactor })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }

    fun getBestProfitFactor(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.profitFactor })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }
    fun getBestSlope(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.testRecord.getProfits().toSequence().simpleRegression().slope })
        println("BEST SLOPE: ${tmp.last()}")
        return tmp.last()
    }
}