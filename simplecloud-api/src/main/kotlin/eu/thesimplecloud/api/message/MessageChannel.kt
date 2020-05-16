package eu.thesimplecloud.api.message

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.utils.IConnectedCloudProcess
import eu.thesimplecloud.clientserverapi.lib.json.GsonCreator
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import java.util.concurrent.CopyOnWriteArraySet


class MessageChannel<T>(private val name: String, private val clazz: Class<T>) : IMessageChannel<T> {

    companion object {
        private val GSON = GsonCreator().create()
    }

    private val listeners = CopyOnWriteArraySet<IMessageListener<T>>()

    override fun registerListener(messageListener: IMessageListener<T>) {
        this.listeners.add(messageListener)
    }

    override fun unregisterListener(messageListener: IMessageListener<T>) {
        this.listeners.remove(messageListener)
    }

    /**
     * Notifies all listeners.
     */
    fun notifyListeners(message: Message) {
        if (message.className != clazz.name)
            throw IllegalArgumentException("Invalid message class on message channel ${message.channel}: Expected ${clazz.name} but was ${message.className} ")
        val jsonData = JsonData.fromJsonString(message.messageString)
        val msg = jsonData.getObject(clazz)
        val connectedProcess = message.senderReference.getConnectedProcess()
                ?: throw IllegalArgumentException("Connected process of ${message.senderReference.name} is null")
        this.listeners.forEach { it.messageReceived(msg, connectedProcess) }
    }

    override fun getName(): String {
        return this.name
    }

    override fun getMessageClass(): Class<T> {
        return this.clazz
    }

    override fun sendMessage(msg: T, receivers: List<IConnectedCloudProcess>) {
        val thisComponent = CloudAPI.instance.getThisSidesCloudProcess()
        val messageString = GSON.toJson(msg)
        val message = Message(getName(), clazz.name, messageString, thisComponent.toNetworkComponentReference(), receivers.map { it.toNetworkComponentReference() })
        return (CloudAPI.instance.getMessageChannelManager() as MessageChannelManager)
                .sendMessage(message)
    }

}