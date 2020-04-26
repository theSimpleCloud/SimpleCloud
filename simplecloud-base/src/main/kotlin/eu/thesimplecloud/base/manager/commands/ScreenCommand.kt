package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

@Command("screen", CommandType.CONSOLE, "simplecloud.command.screen")
class ScreenCommand : ICommandHandler {

    @CommandSubPath("join <name>", "Joins a screen")
    fun screenCommand(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val screen = Launcher.instance.screenManager.getScreen(name)
        if (screen == null) {
            commandSender.sendMessage("manager.command.screen.not-exist", "The specified screen does not exist.")
            return
        }
        screen.getAllSavedMessages().forEach { Launcher.instance.logger.empty(it) }
        Launcher.instance.logger.empty("You joined the screen ${screen.getName()}. To leave the screen write \"leave\"")
        Launcher.instance.logger.empty("All commands will be executed on the screen.")
        Launcher.instance.screenManager.setActiveScreen(screen)
    }

    @CommandSubPath("list", "Lists all screens")
    fun listScreens(commandSender: ICommandSender) {
        commandSender.sendMessage("manager.command.screen.list", "There are following screens available:")
        val screensString = Launcher.instance.screenManager.getAllScreens().joinToString { it.getName() }
        commandSender.sendMessage(screensString)
    }

}