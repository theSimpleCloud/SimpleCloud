package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.base.manager.events.CloudPlayerLoginEvent
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.exception.NoSuchPlayerException
import java.util.*

class PacketInPlayerLoginRequest() : ObjectPacket<UUID>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value ?: return contentException("value")
        val cloudPlayer = CloudLib.instance.getCloudPlayerManager().getCachedCloudPlayer(value) ?: return failure(NoSuchPlayerException("Player cannot be found"))
        val loginEvent = CloudPlayerLoginEvent(cloudPlayer)
        CloudLib.instance.getEventManager().call(loginEvent)
        if (loginEvent.isCancelled()){
            cloudPlayer.kick(loginEvent.kickMessage)
        } else {
            cloudPlayer.sendToLobby()
        }
        return unit()
    }
}