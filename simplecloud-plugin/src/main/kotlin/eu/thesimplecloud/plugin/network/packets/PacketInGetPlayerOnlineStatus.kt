package eu.thesimplecloud.plugin.network.packets

import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.proxy.velocity.CloudVelocityPlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin
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

        if (CloudPlugin.instance.thisService().getServiceVersion() == ServiceVersion.VELOCITY) {
            return success(CloudVelocityPlugin.instance.proxyServer.getPlayer(uuid) != null)
        } else {
            return success(ProxyServer.getInstance().getPlayer(uuid) != null)
        }
    }

}