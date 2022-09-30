package com.nodil.algoquant.core.exchange

interface Connector {

    fun buyMarket(symbol: String, volume: Double)
    fun sellMarket(symbol: String, volume: Double)

    fun getSymbolPrecision(symbol: String): Int


}