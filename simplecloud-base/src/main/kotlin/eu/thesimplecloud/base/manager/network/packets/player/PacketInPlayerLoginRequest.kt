package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.base.manager.events.CloudPlayerLoginRequestEvent
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import java.util.*

class PacketInPlayerLoginRequest() : ObjectPacket<UUID>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(value) ?: return failure(NoSuchPlayerException("Player cannot be found"))
        val loginEvent = CloudPlayerLoginRequestEvent(cloudPlayer)
        CloudAPI.instance.getEventManager().call(loginEvent)
        if (loginEvent.isCancelled()){
            cloudPlayer.kick(loginEvent.kickMessage)
        }
        return unit()
    }
}