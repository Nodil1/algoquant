package com.nodil.algoquant.core.managers.deal

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.bars.BarSeries
import com.nodil.algoquant.core.trader.Deal

abstract class DealManager {
    abstract fun checkDeal(barSeries: com.nodil.algoquant.core.bars.BarSeries, deal: Deal)
}