package eu.thesimplecloud.api.network.packets.message

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.message.Message
import eu.thesimplecloud.api.message.MessageChannelManager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOChannelMessage() : ObjectPacket<Message>() {

    constructor(message: Message) : this() {
        this.value = message
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val message = this.value ?: return contentException("value")
        val messageChannelManager = CloudAPI.instance.getMessageChannelManager()
                as MessageChannelManager
        messageChannelManager.incomingMessage(message)
        return unit()
    }
}