package com.nodil.algoquant.core.indicators.harmonic

open class FivePointPattern(
    open val AB: Array<Double>,
    open val BC: Array<Double>,
    open val CD: Array<Double>,
    open val XD: Array<Double>,
) {

    companion object {
        class Gartley(
            override val AB: Array<Double> = arrayOf(0.618),
            override val BC: Array<Double> = arrayOf(0.382, 0.886),
            override val CD: Array<Double> = arrayOf(1.113, 1.168),
            override val XD: Array<Double> = arrayOf(0.786)
        ) : FivePointPattern(AB, BC, CD, XD)
    }
}