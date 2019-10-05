package eu.thesimplecloud.launcher.logger

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication
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

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.09.2019
 * Time: 17:02
 */
@Suppress("NON_EXHAUSTIVE_WHEN")
class LoggerProvider(var applicationName: String) : Logger("SimpleCloudLogger", null) {

    val dataFormat = SimpleDateFormat("[HH:mm:ss]")

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

    fun success(msg: String) {
        super.info(msg)
        print("\r" + getColoredString(msg, LogType.SUCCESS))
        updatePromt(true)
    }

    override fun info(msg: String) {
        super.info(msg)
        print("\r" + getColoredString(msg, LogType.INFO))
        updatePromt(true)
    }

    override fun warning(msg: String) {
        super.warning(msg)
        print("\r" + getColoredString(msg, LogType.WARNING))
        updatePromt(true)
    }

    override fun severe(msg: String) {
        super.severe(msg)
        print("\r" + getColoredString(msg, LogType.ERROR))
        updatePromt(true)
    }

    fun console(msg: String) {
        super.info(msg)
        print("\r" + getColoredString(msg, LogType.CONSOLE))
        updatePromt(true)
    }

    fun empty(msg: String) {
        super.info(msg)
        print("\r" + getColoredString(msg, LogType.EMPTY))
        updatePromt(true)
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

        var message = "§r" + text + "§r";
        if (!type.equals(LogType.EMPTY)) {
            val time = dataFormat.format(Calendar.getInstance().getTime())
            message = "$time " + logType + ":" + " §r" + text + "§r";
        }

        return AnsiColorHelper.toColoredString(message)
    }

    fun updatePromt(newLine: Boolean) {
        var promt = getColoredString("§c${applicationName}§f@§eSimpleCloud§f>", LogType.EMPTY);
        if (Launcher.instance.setupManager.currentSetup != null) {
            promt = Launcher.instance.setupManager.currentQuestion?.questionName() + ":"
        }
        if (newLine)
            println("")
                    
        print ("\r$promt ")
    }

}