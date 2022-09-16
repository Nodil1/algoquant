package com.algoquant.core.managers.deal

import com.algoquant.core.bars.Bar
import com.algoquant.core.trader.Deal

abstract class DealManager {
    abstract fun checkDeal(bar: Bar, deal: Deal)
}