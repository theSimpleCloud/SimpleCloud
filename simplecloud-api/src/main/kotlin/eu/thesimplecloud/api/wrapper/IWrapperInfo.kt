package eu.thesimplecloud.api.wrapper

import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.utils.IAuthenticatable


interface IWrapperInfo : IAuthenticatable, ICommandExecutable {

    /**
     * Returns the host of this wrapper.
     */
    fun getHost(): String

    /**
     * Returns the amount of services this wrapper can start simultaneously
     */
    fun getMaxSimultaneouslyStartingServices(): Int

    /**
     * Returns the amount of RAM the wrapper uses at the moment in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the amount of RAM this wrapper has at maximum
     */
    fun getMaxMemory(): Int

    /**
     * Returns the amount of RAM the wrapper has left
     */
    fun getUnusedMemory(): Int {
        return getMaxMemory() - getUsedMemory()
    }

    /**
     * Returns whether this wrapper has the specified memory left
     */
    fun hasEnoughMemory(memory: Int): Boolean {
        return getUnusedMemory() >= memory
    }

    /**
     * Returns whether the wrapper has received the templates.
     */
    fun hasTemplatesReceived(): Boolean

}