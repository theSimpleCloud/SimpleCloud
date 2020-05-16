package eu.thesimplecloud.api.message

import com.google.common.collect.Maps
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentReference
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.network.packets.message.PacketIOChannelMessage
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer

class MessageChannelManager : IMessageChannelManager {

    private val channels = Maps.newConcurrentMap<String, MessageChannel<*>>()

    override fun <T> registerMessageChannel(name: String, clazz: Class<T>): IMessageChannel<T> {
        if (this.channels.containsKey(name)) throw IllegalArgumentException("Channel is already registered")
        val messageChannel = MessageChannel(name, clazz)
        this.channels[name] = messageChannel
        return messageChannel
    }

    override fun getMessageChannelByName(name: String): IMessageChannel<*>? {
        return this.channels[name]
    }

    fun sendMessage(message: Message) {
        val packetToSend = PacketIOChannelMessage(message)
        if (CloudAPI.instance.isManager()) {
            val server = CloudAPI.instance.getThisSidesCommunicationBootstrap() as INettyServer<*>
            val allNotManagerReceivers = message.receivers
                    .filter { it.cloudClientType != NetworkComponentType.MANAGER }
                    .mapNotNull { it.getConnectedProcess() }
            allNotManagerReceivers.forEach {
                val client = server.getClientManager().getClientByClientValue(it)
                client?.sendUnitQuery(packetToSend)
            }
        } else {
            val connection = CloudAPI.instance.getThisSidesCommunicationBootstrap() as IConnection
            connection.sendUnitQuery(packetToSend)
        }
    }

    fun incomingMessage(message: Message) {
        val channel = this.channels[message.channel]
        if (CloudAPI.instance.isManager()){
            sendMessage(message)
            if (message.receivers.contains(NetworkComponentReference.MANAGER_COMPONENT))
                channel?.notifyListeners(message)
        } else {
            channel?.notifyListeners(message)
        }
    }

}