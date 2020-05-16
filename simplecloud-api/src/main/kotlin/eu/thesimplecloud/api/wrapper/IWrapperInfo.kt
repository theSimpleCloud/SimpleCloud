package eu.thesimplecloud.api.wrapper


import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.api.utils.IConnectedCloudProcess


interface IWrapperInfo : IConnectedCloudProcess, ISynchronizedListObject {

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

    /**
     * Returns the amount of services this wrapper is currently starting
     */
    fun getCurrentlyStartingServices(): Int

    override fun getNetworkComponentType(): NetworkComponentType = NetworkComponentType.WRAPPER

}