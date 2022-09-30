package com.nodil.algoquant.core.trader

import com.nodil.algoquant.core.bars.Bar

class Target(
    val triggerPrice: Double,
    val reduceSize: Int,
    var eventBar : com.nodil.algoquant.core.bars.Bar? = null
) {

    override fun toString(): String {
        return "Trigger: $triggerPrice | $reduceSize | E.Price ${eventBar?.close} | E.Time ${eventBar?.getDateTime()}"
    }
}