package com.nodil.algoquant.core.tester

class TestResult(
    val name: String
) {

    private val tests = mutableListOf<BackTestResult>()


    fun add(backTestResult: BackTestResult){
        tests.add(backTestResult)
    }
    fun printBest(dealCount: Int = 10){
        val tmp = tests.toList().sortedWith(compareBy({ it.statistic.metric.totalEarn }, { it.statistic.metric.totalCount })).reversed()
        tmp.onEachIndexed { index, entry ->
            if (index < 3) {
                println("====$name====\n${entry.statistic.metric.totalEarn} ${entry.settings}\n\n${entry.statistic.metric}\n\n ${entry.statistic.printDeals()}\n\n==========\n\n")
            }
        }
    }
}