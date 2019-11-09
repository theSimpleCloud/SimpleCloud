package eu.thesimplecloud.lib.wrapper

interface IWritableWrapperInfo : IWrapperInfo {

    /**
     * Sets the amount of services this wrapper can start simultaneously
     */
    fun setMaxSimultaneouslyStartingServices(amount: Int)

    /**
     * Sets the maximum MB of RAM for this wrapper
     */
    fun setMaxMemory(memory: Int)

    /**
     * Sets the amount of RAM in MB the wrapper currently uses
     */
    fun setUsedMemory(memory: Int)

    /**
     * Sets whether the wrapper has received the templates.
     */
    fun setTemplatesReceived(boolean: Boolean)

}