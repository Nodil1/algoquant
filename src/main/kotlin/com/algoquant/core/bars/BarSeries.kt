package com.algoquant.core.bars

import org.ta4j.core.BaseBar
import org.ta4j.core.num.Num
import java.time.Duration
import java.time.ZonedDateTime
import java.util.function.Function

class BarSeries : org.ta4j.core.BarSeries{

    private val bars = mutableListOf<Bar>()
    fun getIterator() = bars.iterator()
    fun getIndices() = bars.indices
    val size: Int
        get() = bars.size

    operator fun get(x: Int) = bars[x]

    operator fun plus(a: BarSeries) : BarSeries {
        a.getIterator().forEach {
            bars.add(it)
        }
        return this
    }

    fun add(element: Bar) {
        if (bars.size > 8000) {
            clearOld()
        }
        bars.add(element)
    }

    fun last(): Bar {
        return bars.last()
    }

    fun getClosePrices(): Array<Double> {
        val result = mutableListOf<Double>()
        bars.onEach { result.add(it.close) }
        return result.toTypedArray()
    }

    fun getOpenPrices(): Array<Double> {
        val result = mutableListOf<Double>()
        bars.onEach { result.add(it.open) }
        return result.toTypedArray()
    }

    fun getHighPrices(): Array<Double> {
        val result = mutableListOf<Double>()
        bars.onEach { result.add(it.high) }
        return result.toTypedArray()
    }

    fun getLowPrices(): Array<Double> {
        val result = mutableListOf<Double>()
        bars.onEach { result.add(it.low) }
        return result.toTypedArray()
    }

    fun getVolumes(): Array<Double> {
        val result = mutableListOf<Double>()
        bars.onEach { result.add(it.close) }
        return result.toTypedArray()
    }

    fun slice(from: Int, to: Int) : BarSeries {
        val tmp = bars.subList(from, to)
        val series = BarSeries()
        tmp.onEach {
            series.add(it)
        }
        return series
    }
    fun getLast(count: Int = 200): BarSeries {
        return this.slice(size - count, size-1)
    }
    private fun clearOld() {
        val expectedOutput = bars.takeLast(bars.size - 1000)
        bars.clear()
        bars.addAll(expectedOutput)
    }

    // Ta4j
    override fun getName(): String {
        return "Series"
    }

    override fun getBar(i: Int): Bar = get(i)

    override fun getBarCount(): Int = size

    override fun getBarData(): MutableList<org.ta4j.core.Bar> = bars as MutableList<org.ta4j.core.Bar>

    override fun getBeginIndex(): Int = 0

    override fun getEndIndex(): Int = bars.lastIndex

    override fun getMaximumBarCount(): Int = bars.size

    override fun setMaximumBarCount(maximumBarCount: Int) {
        return
    }

    override fun getRemovedBarsCount(): Int = 0

    override fun addBar(bar: org.ta4j.core.Bar?, replace: Boolean) {
        add(bar as Bar)
    }

    override fun addBar(timePeriod: Duration?, endTime: ZonedDateTime?) {
        return
    }

    override fun addBar(
        endTime: ZonedDateTime?,
        openPrice: Num?,
        highPrice: Num?,
        lowPrice: Num?,
        closePrice: Num?,
        volume: Num?,
        amount: Num?
    ) {
        return
    }

    override fun addBar(
        timePeriod: Duration?,
        endTime: ZonedDateTime?,
        openPrice: Num?,
        highPrice: Num?,
        lowPrice: Num?,
        closePrice: Num?,
        volume: Num?
    ) {
        return    }

    override fun addBar(
        timePeriod: Duration?,
        endTime: ZonedDateTime?,
        openPrice: Num?,
        highPrice: Num?,
        lowPrice: Num?,
        closePrice: Num?,
        volume: Num?,
        amount: Num?
    ) {
        return    }

    override fun addTrade(tradeVolume: Num?, tradePrice: Num?) {
        return    }

    override fun addPrice(price: Num?) {
        return
    }

    override fun getSubSeries(startIndex: Int, endIndex: Int): org.ta4j.core.BarSeries {
        return slice(startIndex, endIndex)
    }

    override fun numOf(number: Number?): Num {
        return bars.last().closePrice
    }

    override fun function(): Function<Number, Num> {
        return bars.last().closePrice.function()
    }


}