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

package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

@Command("screen", CommandType.CONSOLE, "cloud.command.screen")
class ScreenCommand : ICommandHandler {

    @CommandSubPath("join <name>", "Joins a screen")
    fun screenCommand(commandSender: ICommandSender, @CommandArgument("name") name: String) {
        val screen = Launcher.instance.screenManager.getScreen(name)
        if (screen == null) {
            commandSender.sendMessage("manager.command.screen.not-exist", "The specified screen does not exist.")
            return
        }
        Launcher.instance.clearConsole()
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