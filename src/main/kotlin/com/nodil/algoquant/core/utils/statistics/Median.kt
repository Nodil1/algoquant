package com.nodil.algoquant.core.utils.statistics

class Median {
    companion object{
        fun calc(array: Array<Double>): Double {
            array.sort();
            val n = array.size
            if (n % 2 != 0)
                return array[n / 2];

            return (array[(n - 1) / 2] + array[n / 2]) / 2.0;
        }
    }
}