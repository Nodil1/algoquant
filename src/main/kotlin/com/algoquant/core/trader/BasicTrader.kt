package com.algoquant.core.trader

import com.algoquant.core.bars.Bar
import com.algoquant.core.bars.BarSeries
import com.algoquant.core.exchange.Connector
import com.algoquant.core.managers.deal.DealManager
import com.algoquant.core.managers.money.MoneyManager
import com.algoquant.core.statistic.Statistic
import com.algoquant.core.strategy.Strategy
import com.algoquant.core.strategy.StrategyAction
import com.algoquant.core.strategy.StrategyComment
import kotlin.math.pow
import kotlin.math.roundToInt

class BasicTrader(
    private val strategy: Strategy,
    private val symbolName: String,
    private val moneyManager: MoneyManager? = null,
    private val dealManager: DealManager? = null,
    private val connector: Connector? = null
) {
    private val barSeries = BarSeries()
    private var currentDeal: Deal? = null
    private val lastPrice: Double
        get() = barSeries.last().close
    var state = TraderState.OFF
    val statistic = Statistic()

    init {
        state = TraderState.IDLE
        strategy.allowTrading = true
    }

    fun update(bar: Bar) {
        barSeries.add(bar)
        checkStrategy()
        checkDeal()
    }

    private fun checkDeal() {
        if (state != TraderState.IN_TRADE) {
            return
        }
        val lastTarget = currentDeal!!.targets.last()
        dealManager?.checkDeal(barSeries.last(), currentDeal!!)
        when (currentDeal?.side) {
            DealSide.LONG -> {
                if (lastPrice < currentDeal!!.stopLoss){
                    closeDeal()
                }
                currentDeal!!.targets.onEach {
                    if (it.eventBar == null && lastPrice >= it.triggerPrice) {
                        if (it == lastTarget){
                            it.eventBar = barSeries.last()
                            closeDeal()
                        }  else {
                            handleTarget(it)
                        }
                    }
                }
            }
            DealSide.SHORT -> {
                if (lastPrice > currentDeal!!.stopLoss){
                    closeDeal()
                }
                currentDeal!!.targets.onEach {
                    if (it.eventBar == null && lastPrice <= it.triggerPrice) {
                        if (it == lastTarget){
                            it.eventBar = barSeries.last()
                            closeDeal()
                        }  else {
                            handleTarget(it)
                        }
                    }
                }
            }
            else -> {}
        }
    }
    private fun closeDeal(){
        currentDeal?.addEarn(lastPrice, 100)
        currentDeal?.close = barSeries.last()
        statistic.add(currentDeal!!)
        state = TraderState.IDLE
    }

    private fun handleTarget(target: Target) {
        target.eventBar = barSeries.last()
        currentDeal?.addEarn(lastPrice, target.reduceSize)
    }

    private fun checkStrategy() {
        if (state == TraderState.IN_TRADE) {
            return
        }
        with(strategy.getResult(barSeries)) {
            when (this.action) {
                StrategyAction.OPEN_LONG -> {
                    openDeal(DealSide.LONG, this.targets, this.stopLoss, this.comment)
                }
                StrategyAction.OPEN_SHORT -> {
                    openDeal(DealSide.SHORT, this.targets, this.stopLoss, this.comment)
                }
                else -> {}
            }
        }
    }

    private fun openDeal(side: DealSide, targets: Array<Target>, stopLoss: Double, comment: StrategyComment) {
        if (state == TraderState.IDLE) {
            val allowedMoney = moneyManager?.getDealSizeInDollars(statistic) ?: 100
            if (allowedMoney == 0) return

            val amount = if (connector != null) {
                val precision = connector.getSymbolPrecision(symbolName)
                val rawAmount = allowedMoney / lastPrice

                if (precision != 0) {
                    val afterDot = 10.0.pow(precision)
                    ((rawAmount * afterDot).roundToInt() / afterDot)
                } else {
                    rawAmount.roundToInt().toDouble()
                }
            } else {
                (allowedMoney / lastPrice)
            }

            if (side == DealSide.LONG) {
                connector?.buyMarket(symbolName, amount)
            } else {
                connector?.sellMarket(symbolName, amount)
            }
            currentDeal = Deal(
                entry = barSeries.last(),
                side = side,
                amount = amount,
                commissionPercent = 0.04,
                targets = targets,
                stopLoss = stopLoss,
                strategyComment = comment
            )
            state = TraderState.IN_TRADE
        }

    }
}


