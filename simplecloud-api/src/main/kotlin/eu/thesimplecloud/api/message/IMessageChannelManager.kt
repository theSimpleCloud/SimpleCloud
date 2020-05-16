package eu.thesimplecloud.api.message

interface IMessageChannelManager {

    /**
     * Registers a new [MessageChannel]
     *
     */
    fun <T> registerMessageChannel(name: String, clazz: Class<T>): IMessageChannel<T>

    /**
     * Return the [IMessageChannel] found by the specified [name]
     */
    fun getMessageChannelByName(name: String): IMessageChannel<*>?

}