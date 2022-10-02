package com.nodil.algoquant.core.utils

import org.ta4j.core.Indicator
import org.ta4j.core.num.Num

fun Indicator<Num>.last(): Num {
    return getValue(this.barSeries.endIndex)
}