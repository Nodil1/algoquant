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
import com.algoquant.core.utils.Logger
import kotlin.math.pow
import kotlin.math.roundToInt

class BasicTrader(
    private val strategy: Strategy,
    private val symbolName: String,
    private val moneyManager: MoneyManager? = null,
    private val dealManager: DealManager? = null,
    private val connector: Connector? = null,
    private val logger: Logger? = null
) {
    private val barSeries = BarSeries()
    private var currentDeal: Deal? = null
    private val lastPrice: Double
        get() = barSeries.last().close
    var state = TraderState.OFF
    val statistic = Statistic()

    init {
        logger?.logInfo("Start")
        state = TraderState.IDLE
        strategy.allowTrading = true
    }

    fun update(bar: Bar) {
        logger?.logInfo("New  ${bar.close}. Size ${barSeries.size}")
        try {
            barSeries.add(bar)
            checkStrategy()
            checkDeal()
        } catch (e: Exception){
            logger?.logError(e.stackTrace.toString())
            logger?.logError(e.toString())
        }
    }

    private fun checkDeal() {
        logger?.logInfo("Check deal")
        if (state != TraderState.IN_TRADE) {
            logger?.logInfo("Bot not in trade. Return")
            return
        }
        val lastTarget = currentDeal!!.targets.last()
        dealManager?.checkDeal(barSeries, currentDeal!!)
        when (currentDeal?.side) {
            DealSide.LONG -> {
                logger?.logInfo("Check long")
                if (lastPrice < currentDeal!!.stopLoss){
                    logger?.logInfo("Stop loss long. Last price $lastPrice. Stop ${currentDeal!!.stopLoss}")
                    logger?.logDeal("Stop loss long. Last price $lastPrice. Stop ${currentDeal!!.stopLoss}")
                    closeDeal()
                }
                currentDeal!!.targets.onEach {
                    if (it.eventBar == null && lastPrice >= it.triggerPrice) {
                        if (it == lastTarget){
                            logger?.logInfo("Take profit! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            logger?.logDeal("Take profit! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            it.eventBar = barSeries.last()
                            closeDeal()
                        }  else {
                            logger?.logInfo("Target! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            logger?.logDeal("Target! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            handleTarget(it)
                        }
                    }
                }
            }
            DealSide.SHORT -> {
                logger?.logInfo("Check short")
                if (lastPrice > currentDeal!!.stopLoss){
                    logger?.logInfo("Stop loss short. Last price $lastPrice. Stop ${currentDeal!!.stopLoss}")
                    logger?.logDeal("Stop loss short. Last price $lastPrice. Stop ${currentDeal!!.stopLoss}")
                    closeDeal()
                }
                currentDeal!!.targets.onEach {
                    if (it.eventBar == null && lastPrice <= it.triggerPrice) {
                        if (it == lastTarget){
                            logger?.logInfo("Take profit! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            logger?.logDeal("Take profit! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            it.eventBar = barSeries.last()
                            closeDeal()
                        }  else {
                            logger?.logInfo("Target! Last price $lastPrice. Trigger ${it.triggerPrice}")
                            logger?.logDeal("Target! Last price $lastPrice. Trigger ${it.triggerPrice}")
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
        logger?.logDeal("Close deal. Earn: ${currentDeal?.earn} Close at ${currentDeal?.close}")
        logger?.logDeal("Closed deal: \n $currentDeal")
        state = TraderState.IDLE
    }

    private fun handleTarget(target: Target) {
        logger?.logInfo("Handle target")
        target.eventBar = barSeries.last()
        currentDeal?.addEarn(lastPrice, target.reduceSize)
        logger?.logDeal("Target deal. Earn: ${currentDeal?.earn}")
        logger?.logDeal("Target deal: \n $currentDeal")
    }

    private fun checkStrategy() {
        logger?.logInfo("Check strategy")
        if (state == TraderState.IN_TRADE) {
            logger?.logInfo("Bot in trade. Return")
            return
        }
        with(strategy.getResult(barSeries)) {
            when (this.action) {
                StrategyAction.OPEN_LONG -> {
                    logger?.logInfo("Open long!\n $this")
                    openDeal(DealSide.LONG, this.targets, this.stopLoss, this.comment)
                }
                StrategyAction.OPEN_SHORT -> {
                    logger?.logInfo("Open long!\n $this")
                    openDeal(DealSide.SHORT, this.targets, this.stopLoss, this.comment)
                }
                else -> {}
            }
        }
    }

    private fun openDeal(side: DealSide, targets: Array<Target>, stopLoss: Double, comment: StrategyComment) {
        logger?.logDeal("Open deal! Inputs: Side $side, T: ${targets.joinToString()}, SL: $stopLoss \n$comment")
        if (state == TraderState.IDLE) {
            val allowedMoney = moneyManager?.getDealSizeInDollars(statistic) ?: 100
            logger?.logDeal("Allowed money $allowedMoney")
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
            logger?.logDeal("Amount $amount")

            if (side == DealSide.LONG) {
                logger?.logDeal("Buy market $amount")
                connector?.buyMarket(symbolName, amount)
            } else {
                logger?.logDeal("Sell market $amount")
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
            logger?.logDeal("New deal! \n$currentDeal")

            state = TraderState.IN_TRADE
        }

    }
}


