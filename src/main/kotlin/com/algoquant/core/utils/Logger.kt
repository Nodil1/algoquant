package com.algoquant.core.utils

import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime


class Logger(
    private val pair: String,
    private val botName: String
) {
    private val fileInfo = File("INFO $pair $botName")
    private val fileDeal = File("DEAL $pair-$botName")
    private fun getDate(): String? {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = LocalDateTime.now()
        return formatter.format(date)
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