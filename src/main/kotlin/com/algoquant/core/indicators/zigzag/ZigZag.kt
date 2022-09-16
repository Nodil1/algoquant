package com.algoquant.core.indicators.zigzag

import java.util.logging.Logger

/**
 * Java implementation of MQL ZigZag indicator.
 *
 * @author Andrew Kreimer
 */
class ZigZag(
    private val depth // 12
    : Int, deviation: Int, backstep: Int, point: Double
) {
    var deviation // 5
            = 0
    private val backstep // 3
            : Int
    var point // 0.0001~0.01
            = 0.0

    // indicator buffers
    private lateinit var zigzagBuffer // main buffer
            : DoubleArray
    private lateinit var highMapBuffer // highs
            : DoubleArray
    private lateinit var lowMapBuffer // lows
            : DoubleArray
    private val level = 3 // recounting depth
    private val deviationInPoints // deviation in points
            : Double

    // calculation fields
    private var limit = 0
    private var counterZ = 0
    private var whatlookfor = 0
    private var shift = 0
    private var back = 0
    private var lasthighpos = 0
    private var lastlowpos = 0
    private var `val` = 0.0
    private var res = 0.0
    private var curlow = 0.0
    private var curhigh = 0.0
    private var lasthigh = 0.0
    private var lastlow = 0.0
    private var prevCalculated = 0

    init {
        this.deviation = deviation
        this.backstep = backstep
        this.point = point
        // to use in cycle
        deviationInPoints = deviation * point
    }

    /**
     * Searches for index of the highest bar.
     *
     * @param array
     * @param depth
     * @param startPos
     */
    private fun iHighest(array: DoubleArray, depth: Int, startPos: Int): Int {
        var depth = depth
        var index = startPos

        // --- start index validation
        if (startPos < 0) {
            LOGGER.warning {
                String.format(
                    "Invalid parameter in the function iHighest, startPos = %s",
                    startPos
                )
            }
            return 0
        }

        // --- depth correction if need
        if (startPos - depth < 0) depth = startPos
        var max = array[startPos]

        // --- start searching
        for (i in startPos downTo startPos - depth + 1) {
            if (array[i] > max) {
                index = i
                max = array[i]
            }
        }

        // --- return index of the highest bar
        return index
    }

    /**
     * Searches for index of the lowest bar
     *
     * @param array
     * @param depth
     * @param startPos
     */
    private fun iLowest(array: DoubleArray, depth: Int, startPos: Int): Int {
        var depth = depth
        var index = startPos

        // --- start index validation
        if (startPos < 0) {
            LOGGER.warning {
                String.format(
                    "Invalid parameter in the function iLowest, startPos = %s",
                    startPos
                )
            }
            return 0
        }

        // --- depth correction if need
        if (startPos - depth < 0) depth = startPos
        var min = array[startPos]

        // --- start searching
        for (i in startPos downTo startPos - depth + 1) {
            if (array[i] < min) {
                index = i
                min = array[i]
            }
        }

        // --- return index of the lowest bar
        return index
    }

    /**
     * Performs custom calculation.
     *
     * @param ratesTotal
     * @param high
     * @param low
     */
    fun calculate(ratesTotal: Int, high: DoubleArray, low: DoubleArray) {
        init(ratesTotal)

        // set start position for calculations
        if (prevCalculated == 0) limit = depth

        // ZigZag was already counted before
        calculateIfAlreadyCountedBefore(ratesTotal)

        // searching High and Low
        searchForHighAndLow(ratesTotal, high, low)

        // last preparation
        if (whatlookfor == 0) { // uncertain quantity
            lastlow = 0.0
            lasthigh = 0.0
        } else {
            lastlow = curlow
            lasthigh = curhigh
        }

        // final rejection
        shift = limit
        while (shift < ratesTotal) {
            when (whatlookfor) {
                0 -> searchForPeakOrLawn(high, low)
                PIKE -> searchForPeak()
                SILL -> searchForLawn()
                else -> throw UnsupportedOperationException()
            }
            shift++
        }
    }

    /** Searches for lawn.  */
    private fun searchForLawn() {
        if ((highMapBuffer[shift] != 0.0) && (highMapBuffer[shift] > lasthigh) && (lowMapBuffer[shift] == 0.0)) {
            zigzagBuffer[lasthighpos] = 0.0
            lasthighpos = shift
            lasthigh = highMapBuffer[shift]
            zigzagBuffer[shift] = lasthigh
        }
        if (lowMapBuffer[shift] != 0.0 && highMapBuffer[shift] == 0.0) {
            lastlow = lowMapBuffer[shift]
            lastlowpos = shift
            zigzagBuffer[shift] = lastlow
            whatlookfor = PIKE
        }
    }

    /** Searches for peaks.  */
    private fun searchForPeak() {
        if ((lowMapBuffer[shift] != 0.0) && (lowMapBuffer[shift] < lastlow) && (highMapBuffer[shift] == 0.0)) {
            zigzagBuffer[lastlowpos] = 0.0
            lastlowpos = shift
            lastlow = lowMapBuffer[shift]
            zigzagBuffer[shift] = lastlow
        }
        if (highMapBuffer[shift] != 0.0 && lowMapBuffer[shift] == 0.0) {
            lasthigh = highMapBuffer[shift]
            lasthighpos = shift
            zigzagBuffer[shift] = lasthigh
            whatlookfor = SILL
        }
    }

    /**
     * Searches for peak or lawn.
     *
     * @param high
     * @param low
     */
    private fun searchForPeakOrLawn(high: DoubleArray, low: DoubleArray) {
        if (lastlow == 0.0 && lasthigh == 0.0) {
            if (highMapBuffer[shift] != 0.0) {
                lasthigh = high[shift]
                lasthighpos = shift
                whatlookfor = SILL
                zigzagBuffer[shift] = lasthigh
            }
            if (lowMapBuffer[shift] != 0.0) {
                lastlow = low[shift]
                lastlowpos = shift
                whatlookfor = PIKE
                zigzagBuffer[shift] = lastlow
            }
        }
    }

    /**
     * Searches for high and low.
     *
     * @param ratesTotal
     * @param high
     * @param low
     */
    private fun searchForHighAndLow(ratesTotal: Int, high: DoubleArray, low: DoubleArray) {
        shift = limit
        while (shift < ratesTotal) {
            handleLowFound(low)

            // high
            handleHighFound(high)
            shift++
        }
    }

    /**
     * Handles low found.
     *
     * @param low
     */
    private fun handleLowFound(low: DoubleArray) {
        `val` = low[iLowest(low, depth, shift)]
        if (`val` == lastlow) `val` = 0.0 else {
            lastlow = `val`
            if (low[shift] - `val` > deviationInPoints) `val` = 0.0 else {
                back = 1
                while (back <= backstep) {
                    res = lowMapBuffer[shift - back]
                    if ((res != 0.0) && (res > `val`)) lowMapBuffer[shift - back] = 0.0
                    back++
                }
            }
        }
        if (low[shift] == `val`) lowMapBuffer[shift] = `val` else lowMapBuffer[shift] = 0.0
    }

    /**
     * Handles found high.
     *
     * @param high
     */
    private fun handleHighFound(high: DoubleArray) {
        `val` = high[iHighest(high, depth, shift)]
        if (`val` == lasthigh) `val` = 0.0 else {
            lasthigh = `val`
            if (`val` - high[shift] > deviationInPoints) `val` = 0.0 else {
                back = 1
                while (back <= backstep) {
                    res = highMapBuffer[shift - back]
                    if ((res != 0.0) && (res < `val`)) highMapBuffer[shift - back] = 0.0
                    back++
                }
            }
        }
        if (high[shift] == `val`) highMapBuffer[shift] = `val` else highMapBuffer[shift] = 0.0
    }

    /**
     * Performs calculations if previously done.
     *
     * @param ratesTotal
     */
    private fun calculateIfAlreadyCountedBefore(ratesTotal: Int) {
        var i: Int
        if (prevCalculated > 0) {
            i = ratesTotal - 1

            // searching third extremum from the last uncompleted bar
            while (counterZ < level && i > ratesTotal - 100) {
                res = zigzagBuffer[i]
                if (res != 0.0) counterZ++
                i--
            }
            i++
            limit = i

            // what type of exremum we are going to find
            if (lowMapBuffer[i] != 0.0) {
                curlow = lowMapBuffer[i]
                whatlookfor = PIKE
            } else {
                curhigh = highMapBuffer[i]
                whatlookfor = SILL
            }

            // chipping
            i = limit + 1
            while (i < ratesTotal) {
                zigzagBuffer[i] = 0.0
                lowMapBuffer[i] = 0.0
                highMapBuffer[i] = 0.0
                i++
            }
        }
    }

    /**
     * Initializes variables.
     *
     * @param ratesTotal
     */
    private fun init(ratesTotal: Int) {
        limit = 0
        counterZ = 0
        whatlookfor = 0
        shift = 0
        back = 0
        lasthighpos = 0
        lastlowpos = 0
        `val` = 0.0
        res = 0.0
        curlow = 0.0
        curhigh = 0.0
        lasthigh = 0.0
        lastlow = 0.0
        prevCalculated = 0
        zigzagBuffer = DoubleArray(ratesTotal)
        highMapBuffer = DoubleArray(ratesTotal)
        lowMapBuffer = DoubleArray(ratesTotal)
        if (ratesTotal < 100) LOGGER.warning("Not ebought bars for calculation")
    }

    fun getZigzagBuffer(): DoubleArray {
        return zigzagBuffer.clone()
    }

    fun getHighMapBuffer(): DoubleArray {
        return highMapBuffer.clone()
    }

    fun getLowMapBuffer(): DoubleArray {
        return lowMapBuffer.clone()
    }

    fun calculateTrend(bars: Int, i: Int): String {
        var i = i
        var zzTrend: Trend = Trend.UNCERTAINTY
        do {
            if (highMapBuffer[i] != 0.0) zzTrend = Trend.DOWN else if (lowMapBuffer[i] != 0.0) zzTrend = Trend.UP
            i++
        } while (zzTrend == Trend.UNCERTAINTY && i < bars - 1)
        return zzTrend.name
    }

    companion object {
        private val LOGGER: Logger = Logger.getLogger(ZigZag::class.java.name)

        // auxiliary enumeration
        private const val PIKE = 1 // searching for next high
        private const val SILL = -1 // searching for next low
    }
}