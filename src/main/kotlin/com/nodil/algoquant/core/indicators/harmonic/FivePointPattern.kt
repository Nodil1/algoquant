package com.nodil.algoquant.core.indicators.harmonic

open class FivePointPattern(
    open val AB: Array<Double>,
    open val BC: Array<Double>,
    open val CD: Array<Double>,
    open val XD: Array<Double>,
) {

    companion object {
        val ALL = arrayListOf(
            Gartley(), Butterfly(), AntiButterfly(), AntiGartley(), Bat(), Crab(), Cypher(), Shark()
        )
        class Gartley(
            override val AB: Array<Double> = arrayOf(0.618),
            override val BC: Array<Double> = arrayOf(0.382, 0.886),
            override val CD: Array<Double> = arrayOf(1.113, 1.168),
            override val XD: Array<Double> = arrayOf(0.786)
        ) : FivePointPattern(AB, BC, CD, XD)

        class Butterfly(
            override val AB: Array<Double> = arrayOf(0.786),
            override val BC: Array<Double> = arrayOf(0.382, 0.886),
            override val CD: Array<Double> = arrayOf(1.618, 2.24),
            override val XD: Array<Double> = arrayOf(1.27),
        ) : FivePointPattern(AB, BC, CD, XD)

        class AntiButterfly(
            override val AB: Array<Double> = arrayOf(0.382, 0.618),
            override val BC: Array<Double> = arrayOf(1.13, 2.618),
            override val CD: Array<Double> = arrayOf(1.272),
            override val XD: Array<Double> = arrayOf(0.618, 0.786)
        ) : FivePointPattern(AB, BC, CD, XD)

        class AntiGartley(
            override val AB: Array<Double> = arrayOf(0.618, 0.786),
            override val BC: Array<Double> = arrayOf(1.13, 2.618),
            override val CD: Array<Double> = arrayOf(1.618),
            override val XD: Array<Double> = arrayOf(1.272)
        ) : FivePointPattern(AB, BC, CD, XD)

        class Bat(
            override val AB: Array<Double> = arrayOf(0.382, 0.500),
            override val BC: Array<Double> = arrayOf(0.382, 0.886),
            override val CD: Array<Double> = arrayOf(1.618, 2.618),
            override val XD: Array<Double> = arrayOf(0.886)
        ) : FivePointPattern(AB, BC, CD, XD)

        class Crab(
            override val AB: Array<Double> = arrayOf(0.382, 0.618),
            override val BC: Array<Double> = arrayOf(0.382, 0.886),
            override val CD: Array<Double> =  arrayOf(2.24, 3.618),
            override val XD: Array<Double> =  arrayOf(1.618)
        ) : FivePointPattern(AB, BC, CD, XD)

        class Cypher(
            override val AB: Array<Double> =arrayOf(0.382, 0.618),
            override val BC: Array<Double> = arrayOf(1.13, 1.414),
            override val CD: Array<Double> = arrayOf(1.272, 2.0),
            override val XD: Array<Double> = arrayOf(0.786)
        ) : FivePointPattern(AB, BC, CD, XD)

        class Shark(
            override val AB: Array<Double> = arrayOf(0.382, 0.618),
            override val BC: Array<Double> = arrayOf(1.13, 1.618),
            override val CD: Array<Double> = arrayOf(1.618, 2.24),
            override val XD: Array<Double> = arrayOf(0.886)
        ) : FivePointPattern(AB, BC, CD, XD)
    }
}