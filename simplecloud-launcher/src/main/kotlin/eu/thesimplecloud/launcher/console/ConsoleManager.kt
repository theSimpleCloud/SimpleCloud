package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(val commandManager: CommandManager) : IConsoleManager {

    val consoleSender = ConsoleSender()
    var thread: Thread? = null

    override fun startThread() {
        thread = Thread {
            while (true) {
                val readLine = readLine() ?: continue

                val setup = Launcher.instance.setupManager.currentSetup
                if (setup != null) {
                    if (readLine.equals("exit")) {
                        Launcher.instance.setupManager.cancelSetup()
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