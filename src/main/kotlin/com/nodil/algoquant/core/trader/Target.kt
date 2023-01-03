package com.nodil.algoquant.core.trader

import kotlin.math.abs

class Target(
    val triggerPrice: Double,
    val reduceSize: Int,
    var eventBar : com.nodil.algoquant.core.bars.Bar? = null
) {

    override fun toString(): String {
        return "Trigger: $triggerPrice | $reduceSize | E.Price ${eventBar?.close} | E.Time ${eventBar?.getDateTime()}"
    }

    companion object {
        fun createTargetsRange(from: Double, to: Double, count: Int): Array<Target>{
            val result = mutableListOf<Target>()
            val step = abs(to - from) / count
            for (i in 1 until count + 1){
                if (from < to) {
                    result.add(com.nodil.algoquant.core.trader.Target(from + step * i, 100 / count))
                } else {
                    result.add(com.nodil.algoquant.core.trader.Target(from - step * i, 100 / count))
                }
            }
            return result.toTypedArray()
        }
    }
}