package eu.thesimplecloud.base.manager.screens

import eu.thesimplecloud.lib.screen.ICommandExecutable

interface IScreenManager {


    /**
     * Registers a screen.
     */
    fun registerScreen(name: String, commandExecutable: ICommandExecutable): IScreen

    /**
     * Returns all registered screens.
     */
    fun getAllScreens(): Set<IScreen>

    /**
     * Returns the screen found by the specified name.
     */
    fun getScreen(name: String) = getAllScreens().firstOrNull { it.getName().equals(name, true) }

}