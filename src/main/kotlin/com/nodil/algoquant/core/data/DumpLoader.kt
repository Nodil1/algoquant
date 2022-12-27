package com.nodil.algoquant.core.data

import com.nodil.algoquant.core.bars.Bar
import com.nodil.algoquant.core.bars.BarSeries
import org.json.JSONArray

class DumpLoader {
    companion object {
        fun loadMultiple(array: Array<String>, dir: String): Map<String, BarSeries>{
            val result = mutableMapOf<String, BarSeries>()
            array.onEach {
                result[it] = loadFromJson(dir, it)
                println("Loaded $it Size ${result[it]!!.size} Start ${result[it]!![0].getDateTime()} End ${result[it]!!.last().getDateTime()}")
            }
            return result.toMap()
        }
        fun loadFromJson(dir: String, filename: String): BarSeries {
            try {
                val fileContent =
                    javaClass.getResource("/dumps/$dir/$filename.json")?.readText()

                val root = JSONArray(fileContent)
                println("Raw size ${root.length()}")
                val barSeries = BarSeries()
                for (i in 0 until root.length()){
                    val it = root.getJSONObject(i)
                    barSeries.add(
                        Bar(
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
                return BarSeries()
            }
        }
    }
}