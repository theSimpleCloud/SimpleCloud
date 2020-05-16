package eu.thesimplecloud.api.message

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.IConnectedCloudProcess
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
     * If you wan't to send a message to the manager you must use [IConnectedCloudProcess.MANAGER_CLOUD_PROCESS]
     * @param msg the object to send
     * @param receivers the list of receivers
     */
    fun sendMessage(msg: T, receivers: List<IConnectedCloudProcess>)

    /**
     * Sends a message
     * As receiver a [ICloudService] or [IWrapperInfo] can be used.
     * If you wan't to send a message to the manager you must use [IConnectedCloudProcess.MANAGER_CLOUD_PROCESS]
     * @param msg the object to send
     * @param receiver the receiver
     */
    fun sendMessage(msg: T, receiver: IConnectedCloudProcess) = sendMessage(msg, listOf(receiver))

}