/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.launcher.logging

import eu.thesimplecloud.launcher.screens.IScreenManager
import eu.thesimplecloud.launcher.startup.Launcher
import org.jline.reader.LineReader
import org.jline.utils.InfoCmp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.09.2019
 * Time: 17:02
 */
@Suppress("NON_EXHAUSTIVE_WHEN")
class LoggerProvider(val screenManager: IScreenManager) : Logger("SimpleCloudLogger", null) {

    private val dataFormat = SimpleDateFormat("[HH:mm:ss]")

    private val loggerMessageListeners = ArrayList<ILoggerMessageListener>()

    private val logsDir = File("logs/")

    private val cachedMessages = ArrayList<Pair<String, LogType>>()

    init {
        level = Level.ALL
        useParentHandlers = false

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] [%4$-7s] %5\$s %n")

        if (!logsDir.exists())
            logsDir.mkdirs()

        val file = File("logs/")
        val simpleFormatter = SimpleFormatter()

        val fileHandler = FileHandler(file.canonicalPath + "/simplecloud-log", 5242880, 100, false)
        fileHandler.encoding = StandardCharsets.UTF_8.name()
        fileHandler.level = Level.ALL
        fileHandler.formatter = simpleFormatter

        addHandler(fileHandler)

        deleteOldLogs()
    }

    private fun deleteOldLogs() {
        val allLogFiles = (logsDir.listFiles() ?: emptyArray()).filterNotNull()
        allLogFiles.filter { isOlderThanTenDays(it) }.forEach { it.delete() }
    }

    private fun isOlderThanTenDays(logFile: File): Boolean {
        val tenDaysInMillis = TimeUnit.DAYS.toMillis(10)
        return (System.currentTimeMillis() - logFile.lastModified()) > tenDaysInMillis
    }

    fun addLoggerMessageListener(loggerMessageListener: ILoggerMessageListener) {
        this.loggerMessageListeners.add(loggerMessageListener)
    }

    @Synchronized
    fun success(msg: String) {
        printMessage(msg, LogType.SUCCESS)
    }

    @Synchronized
    override fun info(msg: String) {
        super.info(msg)
        printMessage(msg, LogType.INFO)
    }

    @Synchronized
    override fun warning(msg: String) {
        super.warning(msg)
        printMessage(msg, LogType.WARNING)
    }

    @Synchronized
    override fun severe(msg: String) {
        super.severe(msg)
        printMessage(msg, LogType.ERROR)
    }

    @Synchronized
    fun console(msg: String) {
        super.info(msg)
        printMessage(msg, LogType.CONSOLE)
    }

    @Synchronized
    fun setup(msg: String) {
        super.info(msg)
        printMessage(msg, LogType.SETUP)
    }

    @Synchronized
    fun empty(msg: String) {
        super.info(msg)
        printMessage(msg, LogType.EMPTY)
    }

    @Synchronized
    fun printCachedMessages() {
        cachedMessages.forEach {
            printMessage(it.first, it.second, false)
        }
    }

    private fun printMessage(msg: String, logType: LogType, cache: Boolean = true) {
        if (cache && Launcher.instance.isBaseLoaded
                && (logType != LogType.SETUP && logType != LogType.EMPTY && logType != LogType.WARNING)) {
            if (cachedMessages.size == 50) {
                cachedMessages.removeAt(0)
            }

            cachedMessages.add(Pair(msg, logType))
        }
        if (isMessageBlocked(logType)) {
            return
        }
        val coloredMessage = getColoredString(msg, logType)
        this.loggerMessageListeners.forEach { it.message(msg, logType) }

        val lineReader = Launcher.instance.consoleManager.lineReader

        lineReader.terminal.puts(InfoCmp.Capability.carriage_return)
        lineReader.terminal.writer().println(coloredMessage);
        lineReader.terminal.flush();

        if (lineReader.isReading) {
            lineReader.callWidget(LineReader.REDRAW_LINE)
            lineReader.callWidget(LineReader.REDISPLAY)
        }

    }

    private fun isMessageBlocked(logType: LogType): Boolean {
        if (logType == LogType.WARNING) {
            return false
        }

        if ((screenManager.hasActiveScreen() && logType != LogType.EMPTY)
                || (Launcher.instance.setupManager.hasActiveSetup() && logType != LogType.SETUP)) {
            return true
        }

        return false
    }

    fun getColoredString(text: String, type: LogType): String {
        var logType = ""
        when (type) {
            LogType.SUCCESS -> {
                logType = "§aSUCCESS§7"
            }

            LogType.INFO -> {
                logType = "§3INFO§7"
            }

            LogType.WARNING -> {
                logType = "§6WARNING§7"
            }

            LogType.ERROR -> {
                logType = "§cERROR§7"
            }

            LogType.CONSOLE -> {
                logType = "§3CONSOLE§7"
            }

            LogType.SETUP -> {
                logType = "§3SETUP§7"
            }

        }

        var message = "§r$text§r"
        if (type != LogType.EMPTY) {
            val time = dataFormat.format(Calendar.getInstance().time)
            message = "$time $logType: §r$text§r"
        }

        return AnsiColorHelper.toColoredString(message)
    }

    @Synchronized
    fun exception(cause: Throwable) {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        cause.printStackTrace(pw)
        val stackTraceMessage = sw.buffer.toString()
        this.severe(stackTraceMessage)
    }

}