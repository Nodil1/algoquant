package com.nodil.algoquant.core.tester

class MultipleTestContainer {
    private val tests = mutableListOf<MultipleTestResult>()
    fun add(multipleTestResult: MultipleTestResult){
        tests.add(multipleTestResult)
    }
    fun getBestEarn(): MultipleTestResult {
        val tmp = tests.sortedWith(compareBy { it.summaryEarn })
        println("BEST: ${tmp.last()}")
        return tmp.last()
    }
}