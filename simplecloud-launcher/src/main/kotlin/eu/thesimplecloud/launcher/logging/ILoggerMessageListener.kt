package eu.thesimplecloud.launcher.logging

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

interface ILoggerMessageListener {



    fun message(msg: String, logType: LogType)
}