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

package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.logging.LogType
import eu.thesimplecloud.launcher.startup.Launcher
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
class ConsoleManager(val commandManager: CommandManager, val consoleSender: ConsoleSender) : IConsoleManager {

    var thread: Thread? = null

    val lineReader = createLineReader()
    private val prompt = Launcher.instance.logger.getColoredString("§bSimpleCloud §7>§f ", LogType.EMPTY)

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
                .option(LineReader.Option.INSERT_TAB, false)
                .build()
    }

    override fun startThread() {
        thread = Thread {
            var readLine: String
            try {
                while (!Thread.currentThread().isInterrupted) {
                    readLine = lineReader.readLine(prompt)
                    handleInput(readLine)
                }
            } catch (ex: UserInterruptException) {
            } catch (ex: IllegalStateException) {
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
        if (Launcher.instance.setupManager.hasActiveSetup()) {
            if (readLine.equals("exit", true)) {
                Launcher.instance.setupManager.cancelCurrentSetup()
                return
            }

            Launcher.instance.setupManager.onResponse(readLine.trim())
            return
        }
        if (readLine.isBlank()) return
        //add cloud to execute a cloud command
        try {
            commandManager.handleCommand("cloud $readLine", consoleSender)
        } catch (e: Exception) {
            Launcher.instance.logger.exception(e)
        }
    }


    override fun stopThread() {
        lineReader.terminal.reader().shutdown()
        lineReader.terminal.pause()
        thread?.interrupt()
    }

}