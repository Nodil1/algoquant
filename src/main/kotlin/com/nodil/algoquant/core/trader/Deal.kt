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
    var close: com.nodil.algoquant.core.bars.Bar? = null,
) {
    private val earns: MutableList<Double> = mutableListOf()


    var currentAmount = amount

    val amountInDollars: Double
        get() = entry.close * amount

    val commission: Double
        get() = amountInDollars * (commissionPercent / 100)

    val earn: Double
        get() = earns.toDoubleArray().sum() - commission

    fun addEarn(price: Double, reduceSize: Int) {
        val reduce = currentAmount * (reduceSize / 100)
        currentAmount -= reduce
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
                "Side: $side Earn: $earn\n" +
                "Targets: ${targets.joinToString() }()}\n" +
                "Stop $stopLoss\n\n" +
                "Comment: ${strategyComment.toString()}"
    }


}