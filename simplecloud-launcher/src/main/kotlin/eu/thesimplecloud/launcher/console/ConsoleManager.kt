package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.logging.LogType
import eu.thesimplecloud.launcher.startup.Launcher
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.MaskingCallback
import org.jline.terminal.TerminalBuilder

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(var applicationName: String, val commandManager: CommandManager, private val consoleSender: ConsoleSender) : IConsoleManager {


    var thread: Thread? = null

    val lineReader = createLineReader()
    var prompt = Launcher.instance.logger.getColoredString("§c${applicationName}§f@§eSimpleCloud§f> ", LogType.EMPTY)

    private fun createLineReader() : LineReader {
        val terminal = TerminalBuilder.builder()
                .system(true)
                .streams(System.`in`, System.out)
                .encoding(Charsets.UTF_8)
                .dumb(true)
                .build()

        return LineReaderBuilder.builder()
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
                .build()
    }

    override fun startThread() {
        thread = Thread {
            var readLine = ""
            while(!Thread.currentThread().isInterrupted) {
                readLine = lineReader.readLine("%{$prompt%}")
                while (!readLine.isBlank()) {
                    handleInput(readLine)

                    readLine = lineReader.readLine("%{$prompt%}") ?: continue
                }
            }
        }
        thread?.start()
    }

    private fun handleInput(readLine: String) {
        val screenManager = Launcher.instance.screenManager
        if (screenManager.hasActiveScreen()) {
            if (readLine.equals("leave", true)) {
                Launcher.instance.screenManager.leaveActiveScreen()
                return
            }
            screenManager.getActiveScreen()?.executeCommand(readLine)
            return
        }

        val setup = Launcher.instance.setupManager.currentSetup
        if (setup != null) {
            if (readLine.equals("exit", true)) {
                Launcher.instance.setupManager.cancelCurrentSetup()
                return
            }

            Launcher.instance.setupManager.onResponse(readLine)
            return
        }
        if (readLine.isBlank()) return
        //add cloud to execute a cloud command
        commandManager.handleCommand("cloud $readLine", consoleSender)
    }

    override fun stopThread() {
        lineReader.terminal.pause()
        thread?.interrupt()
    }

}