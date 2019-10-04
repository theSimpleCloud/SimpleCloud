package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.setu.ISetup

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(val application: ICloudApplication, val commandManager: CommandManager) {
    fun start() {
        val consoleSender = ConsoleSender()

        Thread {
            while (application.isRunning()) {
                val readLine = readLine() ?: continue

                val setup = Launcher.instance.setupManager.currentSetup
                if (setup != null) {
                    if (readLine.equals("exit") && !(application is Launcher)) {
                        Launcher.instance.setupManager.cancelSetup()
                        continue
                    }

                    Launcher.instance.setupManager.onResponse(readLine)
                    continue
                }
                commandManager.handleCommand(readLine, consoleSender)
            }
        }.start()
    }

}