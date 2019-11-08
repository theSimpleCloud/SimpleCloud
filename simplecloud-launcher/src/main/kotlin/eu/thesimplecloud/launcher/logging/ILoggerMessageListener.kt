package eu.thesimplecloud.launcher.logging

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

interface ILoggerMessageListener {



    fun success(msg: String)

    fun info(msg: String)

    fun warning(msg: String)

    fun severe(msg: String)

    fun console(msg: String)

}