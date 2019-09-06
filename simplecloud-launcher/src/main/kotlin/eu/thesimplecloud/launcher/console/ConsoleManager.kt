package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.application.CloudApplication

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:29
 */
class ConsoleManager(val application: CloudApplication) {

    fun start() {
        Thread {
            while (application.isRunning()) {
                val readLine = readLine() ?: continue
                Launcher.instance.logger.empty(readLine)
                //commandManager.handleCommand(readLine)
            }
        }.start()
    }

}