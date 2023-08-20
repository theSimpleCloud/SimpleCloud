/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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
import eu.thesimplecloud.launcher.console.command.provider.ServiceCommandSuggestionProvider
import eu.thesimplecloud.launcher.screens.IScreen
import eu.thesimplecloud.launcher.screens.session.ScreenSession
import eu.thesimplecloud.launcher.startup.Launcher

@Command("screen", CommandType.CONSOLE, "cloud.command.screen")
class ScreenCommand : ICommandHandler {

    @CommandSubPath("join <name>", "Joins a screen")
    fun screenCommand(
        commandSender: ICommandSender,
        @CommandArgument("name", ServiceCommandSuggestionProvider::class) name: String
    ) {
        val screen = handleScreenNotExist(commandSender, name) ?: return
        Launcher.instance.screenManager.joinScreen(ScreenSession(screen, ScreenSession.ScreenCloseBehaviour.CLOSE))
    }

    @CommandSubPath("join <name> -reopen", "Joins a screen and reopens the screen every time the service restarts")
    fun screenCommandReopen(
        commandSender: ICommandSender,
        @CommandArgument("name", ServiceCommandSuggestionProvider::class) name: String
    ) {
        val screen = handleScreenNotExist(commandSender, name) ?: return
        Launcher.instance.screenManager.joinScreen(ScreenSession(screen, ScreenSession.ScreenCloseBehaviour.REOPEN))
    }

    @CommandSubPath("join <name> -keep", "Joins a screen and keeps it open after it closes")
    fun screenCommandKeep(
        commandSender: ICommandSender,
        @CommandArgument("name", ServiceCommandSuggestionProvider::class) name: String
    ) {
        val screen = handleScreenNotExist(commandSender, name) ?: return
        Launcher.instance.screenManager.joinScreen(ScreenSession(screen, ScreenSession.ScreenCloseBehaviour.KEEP_OPEN))
    }

    private fun handleScreenNotExist(commandSender: ICommandSender, name: String): IScreen? {
        val screen = Launcher.instance.screenManager.getScreen(name)
        if (screen == null) {
            commandSender.sendProperty("manager.command.screen.not-exist")
            return null
        }
        return screen
    }

    @CommandSubPath("list", "Lists all screens")
    fun listScreens(commandSender: ICommandSender) {
        commandSender.sendProperty("manager.command.screen.list")
        val screensString = Launcher.instance.screenManager.getAllScreens().joinToString { it.getName() }
        commandSender.sendMessage(screensString)
    }

}