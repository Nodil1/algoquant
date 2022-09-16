package com.algoquant.core.trader

import com.algoquant.core.bars.Bar

class Target(
    val triggerPrice: Double,
    val reduceSize: Int,
    var eventBar : Bar? = null
) {

    override fun toString(): String {
        return "Trigger: $triggerPrice | $reduceSize | E.Price ${eventBar?.close} | E.Time ${eventBar?.getDateTime()}"
    }
}