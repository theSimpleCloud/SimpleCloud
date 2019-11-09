package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.startup.Launcher

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(val commandManager: CommandManager, private val consoleSender: ConsoleSender) : IConsoleManager {


    var thread: Thread? = null

    override fun startThread() {
        thread = Thread {
            while (true) {
                val readLine = readLine() ?: continue

                val screenManager = Launcher.instance.screenManager
                if (screenManager.hasActiveScreen()) {
                    if (readLine.equals("leave", true)){
                        screenManager.setActiveScreen(null)
                        Launcher.instance.clearConsole()
                        Launcher.instance.logger.updatePrompt(false)
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
        thread?.interrupt()
    }

}