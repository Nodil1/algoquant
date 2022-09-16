package com.algoquant.core.strategy

class StrategyComment {
    private val map = mutableMapOf<String, String>()

    operator fun get(x: String) : String{
        return map[x] ?: "None"
    }
    operator fun set(x: String, v: String) {
        map[x] = v
    }

    override fun toString(): String {
        return map.toString()
    }
}