package eu.thesimplecloud.launcher.logging

interface ILoggerMessageListener {



    fun message(msg: String, logType: LogType)
}