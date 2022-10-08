package com.nodil.algoquant.core.tester

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
}