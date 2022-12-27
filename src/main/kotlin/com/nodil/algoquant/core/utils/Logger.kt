package com.nodil.algoquant.core.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Logger(
    private val pair: String,
    private val botName: String
) {
    private val fileInfo = File("log/INFO $pair $botName.txt")
    private val fileDeal = File("log/DEAL $pair-$botName.txt")
    private fun getDate(): String? {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return current.format(formatter)
    }
    fun logInfo(message: String){
        val log = "[${getDate()}] [INFO] | $message\n\n"
        println(log)
        fileInfo.appendText(log)
    }
    fun logDeal(message: String){
        val log = "[${getDate()}] [DEAL] | $message\n\n"
        println(log)
        fileDeal.appendText(log)
    }
    fun logError(message: String) {
        val log = "[${getDate()}] [ERROR] | $message\n\n"
        println(log)
        fileInfo.appendText(log)
    }
}