package com.nodil.algoquant.core.utils.statistics

import kotlin.math.pow
import kotlin.math.sqrt

class RMSE {
    companion object {
        fun calc(arr: Array<Double>): Double {
            var square = 0.0
            var mean = 0.0
            var root = 0.0

            for (i in arr.indices) {
                square += arr[i].pow(2.0)
            }

            mean = square / arr.size

            root = sqrt(mean)

            return root
        }
    }
}