package com.nodil.algoquant.core.data

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.bars.BarSeries
import org.json.JSONArray
import org.json.JSONObject

class DumpLoader {
    companion object {
        fun loadFromJson(dir: String, filename: String): com.nodil.algoquant.core.bars.BarSeries {
            try {
                val fileContent =
                    javaClass.getResource("/dumps/$dir/$filename.json")?.readText()

                val root = JSONArray(fileContent)
                val barSeries = com.nodil.algoquant.core.bars.BarSeries()
                root.onEach {
                    it as JSONObject
                    barSeries.add(
                        com.nodil.algoquant.core.bars.Bar(
                            close = it.getDouble("close"),
                            open = it.getDouble("open"),
                            low = it.getDouble("min"),
                            high = it.getDouble("max"),
                            volume = it.getDouble("volume"),
                            barTrades = it.getLong("trades"),
                            timestampStart = it.getLong("timestampStart"),
                            timestampEnd = it.getLong("timestampStop")
                        )
                    )
                }
                return barSeries
            } catch (e: Exception) {
                e.printStackTrace()
                println(filename)
                return com.nodil.algoquant.core.bars.BarSeries()
            }
        }
    }
}