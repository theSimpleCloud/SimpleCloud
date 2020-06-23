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

package eu.thesimplecloud.launcher.screens

import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.CopyOnWriteArraySet

class ScreenManagerImpl : IScreenManager {

    private val screens = CopyOnWriteArraySet<ScreenImpl>()

    private var activeScreen: IScreen? = null

    override fun registerScreen(commandExecutable: ICommandExecutable): IScreen {
        val screen = ScreenImpl(commandExecutable)
        this.screens.add(screen)
        return screen
    }

    override fun unregisterScreen(name: String) {
        this.screens.removeIf { it.getName().equals(name, true) }
    }

    override fun addScreenMessage(commandExecutable: ICommandExecutable, message: String) {
        val screen = getScreen(commandExecutable.getName()) ?: registerScreen(commandExecutable)
        screen as ScreenImpl
        screen.addMessage(message)
        if (activeScreen == screen) {
            Launcher.instance.logger.empty(message)
        }
    }

    override fun getAllScreens(): Set<IScreen> = this.screens

    override fun getActiveScreen(): IScreen? = this.activeScreen

    override fun setActiveScreen(screen: IScreen?) {
        this.activeScreen = screen
    }

    override fun leaveActiveScreen() {
        Launcher.instance.screenManager.setActiveScreen(null)
        Launcher.instance.clearConsole()

        Launcher.instance.logger.printCachedMessages()
    }

}