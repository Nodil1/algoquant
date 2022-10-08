package com.nodil.algoquant.core.utils.statistics

class SharpeRatio {
    companion object {
        fun calc(riskFree: Double, profitPercent: Double, earns: Array<Double>) : Double {
            return (profitPercent - riskFree) / RMSE.calc(earns)
        }
    }
}