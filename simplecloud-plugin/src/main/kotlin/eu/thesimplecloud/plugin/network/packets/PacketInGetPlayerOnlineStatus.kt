package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import net.md_5.bungee.api.ProxyServer
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 10.04.2020
 * Time: 21:32
 */
class PacketInGetPlayerOnlineStatus : ObjectPacket<UUID>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val uuid = this.value ?: return contentException("value")

        return success(ProxyServer.getInstance().getPlayer(uuid) != null)
    }

}