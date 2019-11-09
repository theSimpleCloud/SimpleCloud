package eu.thesimplecloud.launcher.screens

import eu.thesimplecloud.lib.screen.ICommandExecutable

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

}