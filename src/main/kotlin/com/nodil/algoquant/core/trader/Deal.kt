package com.nodil.algoquant.core.trader

import com.nodil.algoquant.core.strategy.StrategyComment
import kotlin.math.abs

class Deal(
    val entry: com.nodil.algoquant.core.bars.Bar,

    val side: DealSide,
    val amount: Double,
    val commissionPercent: Double,
    val targets: Array<Target>,
    var stopLoss: Double,
    val strategyComment: StrategyComment,
) {
    private val earns: MutableList<Double> = mutableListOf()
    var close: com.nodil.algoquant.core.bars.Bar? = null


    var currentAmount = amount

    val amountInDollars: Double
        get() = entry.close * amount

    val commission: Double
        get() = amountInDollars * (commissionPercent / 100)

    val earn: Double
        get() = earns.toDoubleArray().sum() - commission

    val activeTargets: Int
        get()  {
            var result = 0
            targets.onEach {
                if (it.eventBar == null){
                    result++
                }
            }
            return result
        }
    fun close(closeBar: com.nodil.algoquant.core.bars.Bar){
        close = closeBar
        handleEarn(closeBar.close, currentAmount)
        currentAmount = 0.0
    }
    fun addEarn(price: Double, reduceSize: Int) {
        val reduce = amount * (reduceSize.toDouble() / 100.0)
        currentAmount -= reduce
        handleEarn(price, reduce)
    }
    private fun handleEarn(price: Double, reduce: Double ){
        var difference = abs(entry.close - price) * reduce
        when (side) {
            DealSide.LONG -> {
                if (entry.close < price) {
                    difference *= 1
                } else {
                    difference *= -1
                }
            }
            DealSide.SHORT -> {
                if (entry.close > price) {
                    difference *= 1
                } else {
                    difference *= -1
                }
            }
        }
        earns.add(difference)
    }

    override fun toString(): String {
        return "Entry ${entry.close} ${entry.getDateTime()}\n" +
                "Close ${close?.close} ${close?.getDateTime()}\n" +
                "CurrentAmount: $currentAmount\n"+
                "CurrentAmount: $amount\n"+
                "Side: $side Earn: $earn\n" +
                "Earns: ${earns.joinToString()}\n" +
                "Targets: ${targets.joinToString() }()}\n" +
                "Stop $stopLoss\n" +
                "Comment: ${strategyComment.toString()}\n\n"
    }


}