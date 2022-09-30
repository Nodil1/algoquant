package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.bars.BarSeries
import java.io.File

class TestRecord {
    private val prices = BarSeries()
    private val profits = mutableListOf<Double>()
    val dropDown: Double
        get() = maxDropDown()
    val maxProfit: Double
        get() = profits.maxOrNull() ?: 0.0
    val minProfit: Double
        get() = profits.minOrNull() ?: 0.0

    operator fun plus(testRecord: TestRecord) {
        for (i in testRecord.profits.indices) {
            if (profits.getOrNull(i) == null) {
                profits.add(i, testRecord.profits[i])
            } else {
                profits[i] += testRecord.profits[i]
            }
        }
    }

    fun add(bar: Bar, earn: Double) {
        prices.add(bar)
        profits.add(earn)
    }

    fun toCsv(filename: String) {
        val f = File("out/$filename.csv")
        println("SIZE! ${prices.size}")
        for (i in prices.getIndices()) {
            val p = prices[i].close.toString().replace(".", ",")
            val e = profits[i].toString().replace(".", ",")
            f.appendText("$p;$e\n")
        }
    }

    private fun maxDropDown(): Double {
        var maxProfit = 0.0
        var currentDropDown = 0.0
        val dropDowns = arrayListOf<Double>()
        profits.onEach {
            if (it > maxProfit) {
                dropDowns.add(currentDropDown)
                maxProfit = it
            } else if (it < maxProfit) {
                currentDropDown = maxProfit - it
            }
        }
        val maxDropDown = dropDowns.maxOrNull() ?: 0.0
        val dif = maxProfit - profits.last()
        return if(maxDropDown < dif) {
            -dif
        } else {
            -maxDropDown
        }
    }
}