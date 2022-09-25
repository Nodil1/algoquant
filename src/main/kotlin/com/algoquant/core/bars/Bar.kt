package com.algoquant.core.bars

import org.ta4j.core.BaseBar
import java.sql.Date
import java.text.SimpleDateFormat

class Bar(
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val volume: Double,
    val timestamp: Long
) {
    fun getDateTime(): String {
        val sdf =  SimpleDateFormat("dd MMMM yyyy, HH:mm:ss")
        val netDate = Date(timestamp )
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
}