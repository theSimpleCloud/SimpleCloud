package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.logging.LogType
import eu.thesimplecloud.launcher.startup.Launcher
import org.fusesource.jansi.Ansi

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(var applicationName: String, val commandManager: CommandManager, val consoleSender: ConsoleSender) : IConsoleManager {


    var thread: Thread? = null

    val lineReader = createLineReader()
    var prompt = buildPrompt()

    fun buildPrompt() = Launcher.instance.logger.getColoredString("§c${applicationName}§f@§eSimpleCloud§f> ", LogType.EMPTY)

    private fun createLineReader(): LineReader {
        val consoleCompleter = ConsoleCompleter(this)

        val terminal = TerminalBuilder.builder()
                .system(true)
                .streams(System.`in`, System.out)
                .encoding(Charsets.UTF_8)
                .dumb(true)
                .build()

        return LineReaderBuilder.builder()
                .completer(consoleCompleter)
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
                .build()
    }

    override fun startThread() {

        var prompt = buildPrompt()

        thread = Thread {
            var readLine = ""
            try {
                while (!Thread.currentThread().isInterrupted) {
                    readLine = lineReader.readLine(prompt)
                    handleInput(readLine)
                }
            } catch (ex: UserInterruptException) {
            }  catch (ex: IllegalStateException) {
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
        lineReader.terminal.reader().shutdown()
        lineReader.terminal.pause()
        thread?.interrupt()
    }

}