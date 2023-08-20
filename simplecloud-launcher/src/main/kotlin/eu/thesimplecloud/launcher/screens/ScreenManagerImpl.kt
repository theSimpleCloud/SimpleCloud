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

package eu.thesimplecloud.launcher.screens

import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.launcher.screens.session.ScreenSession
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.CopyOnWriteArraySet

class ScreenManagerImpl : IScreenManager {

    private val screens = CopyOnWriteArraySet<ScreenImpl>()

    private var activeSession: ScreenSession? = null

    override fun registerScreen(commandExecutable: ICommandExecutable): IScreen {
        val screen = ScreenImpl(commandExecutable)
        this.screens.add(screen)

        joinScreenIfActive(screen)

        return screen
    }

    override fun unregisterScreen(name: String) {
        this.screens.removeIf { it.getName().equals(name, true) }
    }

    override fun addScreenMessage(commandExecutable: ICommandExecutable, message: String) {
        val screen = getScreen(commandExecutable.getName()) ?: registerScreen(commandExecutable)
        screen as ScreenImpl
        screen.addMessage(message)
        if (activeSession?.screen == screen) {
            Launcher.instance.logger.empty(message)
        }
    }

    override fun getAllScreens(): Set<IScreen> = this.screens

    override fun getActiveScreenSession(): ScreenSession? {
        return this.activeSession
    }

    override fun joinScreen(screenSession: ScreenSession) {
        this.activeSession = screenSession

        Launcher.instance.clearConsole()
        val screen = screenSession.screen
        screen.getAllSavedMessages().forEach { Launcher.instance.logger.empty(it) }
        Launcher.instance.logger.empty("You joined the screen ${screen.getName()}. To leave the screen write \"leave\"")
        Launcher.instance.logger.empty("All commands will be executed on the screen.")
    }

    override fun leaveActiveScreen() {
        this.activeSession = null
        Launcher.instance.clearConsole()

        Launcher.instance.logger.printCachedMessages()
    }

    private fun joinScreenIfActive(screen: IScreen) {
        val activeSession = this.activeSession ?: return
        if (activeSession.screenCloseBehaviour != ScreenSession.ScreenCloseBehaviour.REOPEN) return
        if (activeSession.screen.getName().equals(screen.getName(), true)) {
            joinScreen(ScreenSession(screen, ScreenSession.ScreenCloseBehaviour.REOPEN))
        }
    }

}