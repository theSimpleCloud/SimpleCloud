package eu.thesimplecloud.launcher.logging

import eu.thesimplecloud.launcher.screens.IScreenManager
import eu.thesimplecloud.launcher.startup.Launcher
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.collections.ArrayList


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.09.2019
 * Time: 17:02
 */
@Suppress("NON_EXHAUSTIVE_WHEN")
class LoggerProvider(var applicationName: String, val screenManager: IScreenManager) : Logger("SimpleCloudLogger", null) {

    val dataFormat = SimpleDateFormat("[HH:mm:ss]")

    private val loggerMessageListeners = ArrayList<ILoggerMessageListener>()

    init {
        level = Level.ALL
        useParentHandlers = false

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\$tT] [%4$-7s] %5\$s %n")

        if (!Files.exists(Paths.get("logs")))
            Files.createDirectory(Paths.get("logs"))

        val file = File("logs/")
        val simpleFormatter = SimpleFormatter()

        val fileHandler = FileHandler(file.canonicalPath + "/simplecloud-log", 5242880, 100, false)
        fileHandler.encoding = StandardCharsets.UTF_8.name()
        fileHandler.level = Level.ALL
        fileHandler.formatter = simpleFormatter

        addHandler(fileHandler)
    }

    fun addLoggerMessageListener(loggerMessageListener: ILoggerMessageListener) {
        this.loggerMessageListeners.add(loggerMessageListener)
    }

    @Synchronized
    fun success(msg: String) {
        super.info(msg)
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
    fun empty(msg: String) {
        super.info(msg)
        printMessage(msg, LogType.EMPTY)
    }

    private fun printMessage(msg: String, logType: LogType) {
        if (isMessageBlocked(logType))
            return
        val coloredMessage = getColoredString(msg, logType)
        this.loggerMessageListeners.forEach { it.message(msg, logType) }

        val lineReader = Launcher.instance.consoleManager.lineReader
        if (lineReader.isReading) {
            lineReader.callWidget(LineReader.CLEAR)
            lineReader.terminal.puts(InfoCmp.Capability.carriage_return)
            lineReader.terminal.writer().println("\r" + coloredMessage)
            lineReader.callWidget(LineReader.REDRAW_LINE)
            lineReader.callWidget(LineReader.REDISPLAY)
        } else {
            lineReader.terminal.writer().println(coloredMessage);
        }

        lineReader.terminal.writer().flush()

        //print("\r" + coloredMessage)
        //updatePrompt(true)
    }

    private fun isMessageBlocked(logType: LogType): Boolean {
        return screenManager.hasActiveScreen() && logType != LogType.EMPTY
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