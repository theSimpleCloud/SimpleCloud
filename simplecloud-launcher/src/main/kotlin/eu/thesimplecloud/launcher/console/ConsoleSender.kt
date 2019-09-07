package eu.thesimplecloud.launcher.console

import eu.thesimplecloud.launcher.Launcher
import eu.thesimplecloud.launcher.console.command.ICommandSender

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 07.09.2019
 * Time: 14:37
 */
class ConsoleSender: ICommandSender {
    override fun sendMessage(message: String) {
        if (message.startsWith("§c") || message.startsWith("&c")) {
            Launcher.instance.logger.warning(message.replace("§c", ""))
        } else {
            Launcher.instance.logger.console(message)
        }
    }
}