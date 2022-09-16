package com.algoquant.core.statistic

class Metric(
    var longProfit: Double = 0.0,
    var longProfitCount: Int = 0,
    var longLoss: Double = 0.0,
    var longLossCount: Int = 0,

    var shortProfit: Double = 0.0,
    var shortProfitCount: Int = 0,
    var shortLoss: Double = 0.0,
    var shortLossCount: Int = 0,

    ) {
    val longCount: Int
        get() = longLossCount + longProfitCount
    val shortCount: Int
        get() = shortLossCount + shortProfitCount
    val totalCount: Int
        get() = longCount + shortCount
    val totalLong: Double
        get() = longLoss + longProfit
    val totalShort: Double
        get() = shortLoss + shortProfit
    val totalEarn: Double
        get() = totalLong + totalShort

    val profitLossRatio: Double
        get() {
            var profitCount = longProfitCount + shortProfitCount
            var lossCount = longLossCount + shortLossCount


            if (profitCount == 0) profitCount = 1
            if (lossCount == 0) lossCount = 1


            return profitCount.toDouble() / totalCount.toDouble()
        }

    val profitLossSize: Double
        get() {
            var profitCount = longProfitCount + shortProfitCount // 3
            val profit = longProfit + shortProfit
            var lossCount = longLossCount + shortLossCount // 2
            val loss = longLoss + shortLoss


            if (profitCount == 0) profitCount = 1
            if (lossCount == 0) lossCount = 1
            val medianProfit = profit / profitCount
            val medianLoss = loss / lossCount
            val ratio = medianProfit / medianLoss
            return if (profit > loss){
                ratio
            } else{
                -ratio
            }
        }

    override fun toString(): String {
        return "===METRIC===\n" +
                "==Long==\n" +
                "LongProfit: $longProfit$ +$longProfitCount\n" +
                "LongLoss: $longLoss$ -$longLossCount\n" +
                "Total long: $totalLong\n"+
                "==Short==\n" +
                "ShortProfit: $shortProfit$ +$shortProfitCount\n" +
                "ShortLoss: $shortLoss$ -$shortLossCount\n" +
                "Total short: $totalShort\n" +
                "===Summary===\n" +
                "Total: ${totalEarn}$ $totalCount\n" +
                "ProfitLossRatio: $profitLossRatio\n" +
                "ProfitLossSize: $profitLossSize"
    }
}