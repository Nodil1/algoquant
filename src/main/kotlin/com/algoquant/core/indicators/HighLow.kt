package com.algoquant.core.indicators

class HighLow(
    private val lockBackRight: Int,
    private val lookBackLeft: Int
) {
    fun findHigh(points: Array<Double>): Array<Int> {
        if (points.size < lookBackLeft){
            return arrayOf()
        }
        val result = mutableListOf<Int>()
        for (i in lookBackLeft  .. points.size - lockBackRight){
            val localArray = mutableListOf<Double>()
            for (k in i - lookBackLeft until  i){
                localArray.add(points[k])
            }
            for (k in i until i + lockBackRight){
                localArray.add(points[k])
            }
            if (localArray.maxOrNull()!! == points[i]){
                result.add(i)
            }
        }
        return result.toTypedArray()
    }

    fun findLow(points: Array<Double>): Array<Int> {
        if (points.size < lookBackLeft){
            return arrayOf()
        }
        val result = mutableListOf<Int>()
        for (i in lookBackLeft  .. points.size - lockBackRight){
            val localArray = mutableListOf<Double>()
            for (k in i - lookBackLeft until  i){
                localArray.add(points[k])
            }
            for (k in i until i + lockBackRight){
                localArray.add(points[k])
            }
            if (localArray.minOrNull()!! == points[i]){
                result.add(i)
            }
        }
        return result.toTypedArray()
    }
}