package com.algoquant.core.statistic

import com.algoquant.core.trader.Deal
import com.algoquant.core.trader.DealSide

class Statistic {
    private val deals = mutableListOf<Deal>()
    val summaryEarn: Double
    get() {
        var sum = 0.0
        deals.onEach {
            sum += it.earn
        }
        return sum
    }
    val metric: Metric
    get()  {
        return with(Metric()){
            deals.onEach {
                when(it.side){
                    DealSide.LONG -> {
                        if(it.earn > 0){
                            this.longProfit += it.earn
                            this.longProfitCount++
                        } else {
                            this.longLoss += it.earn
                            this.longLossCount++
                        }
                    }
                    DealSide.SHORT -> {
                        if(it.earn > 0){
                            this.shortProfit += it.earn
                            this.shortProfitCount++
                        } else {
                            this.shortLoss += it.earn
                            this.shortLossCount++
                        }                    }
                }
            }
            this
        }

    }
    fun add(deal: Deal){
        deals += deal
    }
    fun printDeals(){
        deals.onEach {
            println(it)
        }
    }
}