/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

interface IScreenManager {


    /**
     * Registers a screen.
     */
    fun registerScreen(commandExecutable: ICommandExecutable): IScreen

    /**
     * Unregisters the screen found by the specified name.
     */
    fun unregisterScreen(name: String)

    /**
     * Returns all registered screens.
     */
    fun getAllScreens(): Set<IScreen>

    /**
     * Adds a message to the screen found by the specified name.
     */
    fun addScreenMessage(commandExecutable: ICommandExecutable, message: String)

    /**
     * Returns the screen found by the specified name.
     */
    fun getScreen(name: String) = getAllScreens().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the active screen.
     */
    fun getActiveScreen(): IScreen?

    /**
     * Sets the active screen.
     */
    fun setActiveScreen(screen: IScreen?)

    /**
     * Returns whether a screen is active.
     */
    fun hasActiveScreen(): Boolean = getActiveScreen() != null

    /**
     * Leaves the active screen.
     */
    fun leaveActiveScreen()

}