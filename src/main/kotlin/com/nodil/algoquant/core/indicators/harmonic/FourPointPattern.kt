package com.nodil.algoquant.core.indicators.harmonic

open class FourPointPattern(
    open public val BC: Array<Double>,
    open val CD: Array<Double>
) {
    companion object Patterns {
        class ABCD(
            override  val BC: Array<Double> = arrayOf(0.618),
            override val CD: Array<Double> = arrayOf(1.27)
        ) : FourPointPattern(BC, CD)
    }
}