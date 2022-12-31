package com.nodil.algoquant.core.bars

import org.ta4j.core.num.DoubleNum
import org.ta4j.core.num.Num
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

class Bar(
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Double,
    val barTrades: Long,
    val timestampStart: Long,
    val timestampEnd: Long,

    ) : org.ta4j.core.Bar{
    private var amountTraded : Double = 0.0
    init {
        amountTraded = volume * close
    }
    fun getDateTime(): String {

        val sdf =  SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
        val netDate = Date(timestampStart)
        return sdf.format(netDate)
    }

    override fun toString(): String {
        return "====Bar====\n" +
                "Open: $open\n" +
                "Close: $close\n" +
                "High: $high\n" +
                "Low: $low\n" +
                "Vol: $volume\n" +
                "Time: ${getDateTime()}\n\n"
    }

    override fun getOpenPrice(): DoubleNum = DoubleNum.valueOf(open)

    override fun getLowPrice(): DoubleNum = DoubleNum.valueOf(low)

    override fun getHighPrice(): DoubleNum = DoubleNum.valueOf(high)

    override fun getClosePrice(): DoubleNum = DoubleNum.valueOf(close)

    override fun getVolume(): DoubleNum = DoubleNum.valueOf(volume)

    override fun getTrades(): Long = 100

    override fun getAmount(): DoubleNum = DoubleNum.valueOf(volume)

    override fun getTimePeriod(): Duration = Duration.between(beginTime, endTime)

    override fun getBeginTime(): ZonedDateTime {
        val triggerTimeStart = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestampStart),
            TimeZone.getDefault().toZoneId()
        )
        return triggerTimeStart.atZone(ZoneId.systemDefault())
    }

    override fun getEndTime(): ZonedDateTime {
        val triggerTimeEnd = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestampEnd),
            TimeZone.getDefault().toZoneId())

        return triggerTimeEnd.atZone(ZoneId.systemDefault())
    }

    override fun addTrade(tradeVolume: Num?, tradePrice: Num?) {
        return
    }

    override fun addPrice(price: Num?) {
        return
    }
}