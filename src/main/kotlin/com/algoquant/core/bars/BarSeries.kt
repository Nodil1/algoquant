package com.algoquant.core.bars

class BarSeries {

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


}