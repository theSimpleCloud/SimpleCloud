package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.logging.LogType
import eu.thesimplecloud.launcher.startup.Launcher
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(val commandManager: CommandManager, private val consoleSender: ConsoleSender) : IConsoleManager {


    var thread: Thread? = null

    val lineReader = createLineReader()
    var prompt = Launcher.instance.logger.getColoredString("§c${Launcher.instance.logger.applicationName}§f@§eSimpleCloud§f> ", LogType.EMPTY)

    private fun createLineReader() : LineReader {
        val terminal = TerminalBuilder.builder()
                .system(true)
                .streams(System.`in`, System.out)
                .encoding(Charsets.UTF_8)
                .build()

        return LineReaderBuilder.builder()
                .terminal(terminal)
                .build()
    }

    override fun startThread() {
        thread = Thread {
            while (true) {
                val readLine = lineReader.readLine("%{$prompt%}") ?: continue

                val screenManager = Launcher.instance.screenManager
                if (screenManager.hasActiveScreen()) {
                    if (readLine.equals("leave", true)){
                        Launcher.instance.screenManager.leaveActiveScreen()
                        continue
                    }
                    screenManager.getActiveScreen()?.executeCommand(readLine)
                    continue
                }

                val setup = Launcher.instance.setupManager.currentSetup
                if (setup != null) {
                    if (readLine.equals("exit", true)) {
                        Launcher.instance.setupManager.cancelCurrentSetup()
                        continue
                    }

                    Launcher.instance.setupManager.onResponse(readLine)
                    continue
                }
                commandManager.handleCommand(readLine, consoleSender)
            }
        }
        thread?.start()
    }

    override fun stopThread() {
        lineReader.terminal.pause()
        thread?.interrupt()
    }

}