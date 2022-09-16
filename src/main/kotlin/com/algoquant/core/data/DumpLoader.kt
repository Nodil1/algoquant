package com.algoquant.core.data

import com.algoquant.core.bars.Bar
import com.algoquant.core.bars.BarSeries
import org.json.JSONArray
import org.json.JSONObject

class DumpLoader {
    companion object {
        fun loadFromJson(dir: String, filename: String): BarSeries {
            val fileContent = javaClass.getResource("/dumps/$dir/$filename.json")?.readText()
            val root = JSONArray(fileContent)
            val barSeries = BarSeries()
            root.onEach {
                it as JSONObject
                barSeries.add(
                    Bar(
                        close = it.getDouble("close"),
                        open = it.getDouble("open"),
                        low = it.getDouble("min"),
                        high = it.getDouble("max"),
                        volume = it.getDouble("volume"),
                        timestamp = it.getLong("timestamp")
                    )
                )
            }
            return barSeries
        }
    }
}