package com.algoquant.core.indicators.ta4j

import com.algoquant.core.bars.Bar
import com.algoquant.core.bars.BarSeries
import org.ta4j.core.BaseBar
import org.ta4j.core.BaseBarSeries
import org.ta4j.core.BaseBarSeriesBuilder
import org.ta4j.core.Indicator
import org.ta4j.core.indicators.CachedIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.Num
import java.time.*
import java.util.*
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime


class WrappedIndicator(private val clazz: Class<*>, val param: Int) {
    var indicator: CachedIndicator<Num>? = null

    fun calculate(barSeries: BarSeries) {

        val closePrice = ClosePriceIndicator(createBarSeries(barSeries))
        indicator = clazz.getConstructor(Indicator::class.java, Int::class.java)
            .newInstance(closePrice, param) as CachedIndicator<Num>

    }

    fun get(i: Int): Num {
        return indicator!!.getValue(i)
    }

    private fun fillSeries(barSeries: BarSeries){
        barSeries.getIterator().forEach {
            //cachedSeris.addBar(createBar(it))
        }
    }
    private fun createBarSeries(barSeries: BarSeries): BaseBarSeries {
        val result = BaseBarSeriesBuilder().withName("series").withNumTypeOf(DoubleNum::valueOf).build()
        barSeries.getIterator().forEach {
            result.addBar(createBar(it))
        }
        return result
    }


    private fun createBar(bar: Bar) : BaseBar {
        val triggerTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(bar.timestamp),
            TimeZone.getDefault().toZoneId())

        val zonedDateTime: ZonedDateTime = triggerTime.atZone(ZoneId.systemDefault())
        return BaseBar.builder(DoubleNum::valueOf, Double::class.java)
            .timePeriod(Duration.ofDays(1))
            .endTime(zonedDateTime)
            .openPrice(bar.open)
            .highPrice(bar.high)
            .lowPrice(bar.low)
            .closePrice(bar.close)
            .volume(bar.volume)
            .build()
    }

}
