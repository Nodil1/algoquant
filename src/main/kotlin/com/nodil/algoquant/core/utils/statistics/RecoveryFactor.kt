package com.nodil.algoquant.core.utils.statistics

class RecoveryFactor {
    companion object{
        fun calc(profit: Double, maxDropDown: Double): Double {
            if (profit == 0.0) return 0.0
            if (maxDropDown == 0.0) return 0.0
            return profit/maxDropDown
        }
    }
}