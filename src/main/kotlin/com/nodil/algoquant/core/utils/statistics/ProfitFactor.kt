package com.nodil.algoquant.core.utils.statistics

class ProfitFactor {
    companion object{
        fun calc(profit: Double, loss: Double): Double {
            if (loss == 0.0 && profit == 0.0) return 0.0
            if (profit == 0.0) return 1/loss
            if (loss == 0.0) return profit/1
            return profit/loss
        }
    }
}