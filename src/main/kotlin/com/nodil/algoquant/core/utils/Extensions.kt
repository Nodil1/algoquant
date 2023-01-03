package com.nodil.algoquant.core.utils

import org.ta4j.core.Indicator
import org.ta4j.core.num.Num
import kotlin.math.round

fun Indicator<Num>.last(): Num {
    return getValue(this.barSeries.endIndex)
}

fun Double.roundToTwoDecimal(): Double{
    return round(this * 100) / 100
}
fun Array<Double>.toSequence(): Sequence<Pair<Int, Double>> {
    val seq = mutableListOf<Pair<Int, Double>>()
    this.onEachIndexed() { idx, it ->
        seq.add(Pair(idx, it))
    }
    return seq.asSequence()
}