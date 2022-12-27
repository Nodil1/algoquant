package com.nodil.algoquant.core.tester

import com.nodil.algoquant.core.bars.BarSeries
import java.io.File

class TestRecord() {
    private val profits = mutableListOf<Short>()
    var prices = BarSeries()

    val dropDown: Double
        get() = maxDropDown()
    val maxProfit: Double
        get() = (profits.maxOrNull() ?: 0.0).toDouble()
    val minProfit: Double
        get() = (profits.minOrNull() ?: 0.0).toDouble()




    operator fun plus(testRecord: TestRecord) {
        for (i in 0 until testRecord.profits.size) {
            if (profits.getOrNull(i) == null) {
                profits.add(i, testRecord.profits[i])
                prices.add(testRecord.prices[i])
            } else {
                profits[i] = (testRecord.profits[i] + profits[i]).toShort()
            }
        }
    }
    fun getProfits() : Array<Double>{
        val result = mutableListOf<Double>()
        profits.onEach {
            result += it.toDouble()
        }
        return result.toTypedArray()
    }
    fun add(earn: Double) {
        //prices.add(bar)
        profits.add(earn.toInt().toShort())
    }

    fun toCsv(filename: String) {
        val f = File("csv/$filename.csv")
        println("SIZE! ${prices.size} ${profits.size}")
        for (i in 0 until prices.size - 2) {
            val p = prices[i].close.toString().replace(".", ",")
            val e = profits[i].toString().replace(".", ",")
            val date = prices[i].getDateTime()
            f.appendText("$p;$e;$date\n")
        }
    }

    private fun maxDropDown(): Double {
        var maxProfit: Short = 0
        var currentDropDown: Short = 0
        val dropDowns = arrayListOf<Short>()
        for (i in 0 until prices.size) {
            val it = profits[i]
            if (it > maxProfit) {
                dropDowns.add(currentDropDown)
                maxProfit = it
            } else if (it < maxProfit) {
                if (maxProfit - it > currentDropDown) {
                    currentDropDown = (maxProfit - it).toShort()
                }
            }
        }
        dropDowns.add(currentDropDown)
        val maxDropDown = dropDowns.maxOrNull() ?: 0
        val dif = maxProfit - profits.last()
        return if (maxDropDown < dif) {
            dif.toDouble()
        }
        else {
            maxDropDown.toDouble()
        }
    }
}