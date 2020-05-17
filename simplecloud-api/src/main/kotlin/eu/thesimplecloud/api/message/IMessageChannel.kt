package eu.thesimplecloud.api.message

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.INetworkComponent
import eu.thesimplecloud.api.utils.Nameable
import eu.thesimplecloud.api.wrapper.IWrapperInfo

interface IMessageChannel<T> : Nameable {

    /**
     * Returns the class of this [IMessageChannel]
     */
    fun getMessageClass(): Class<T>

    /**
     * Registers a listener
     */
    fun registerListener(messageListener: IMessageListener<T>)

    /**
     * Unregisters a listener
     */
    fun unregisterListener(messageListener: IMessageListener<T>)

    /**
     * Sends a message
     * As receiver a [ICloudService] or [IWrapperInfo] can be used.
     * If you wan't to send a message to the manager you must use [INetworkComponent.MANAGER_COMPONENT]
     * @param msg the object to send
     * @param receivers the list of receivers
     */
    fun sendMessage(msg: T, receivers: List<INetworkComponent>)

    /**
     * Sends a message
     * As receiver a [ICloudService] or [IWrapperInfo] can be used.
     * If you wan't to send a message to the manager you must use [INetworkComponent.MANAGER_COMPONENT]
     * @param msg the object to send
     * @param receiver the receiver
     */
    fun sendMessage(msg: T, receiver: INetworkComponent) = sendMessage(msg, listOf(receiver))

}