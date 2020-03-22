package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.network.packets.player.PacketIOUpdateCloudPlayer
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class PacketInPlayerConnectToServer() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val uniqueId = this.jsonData.getObject("playerUniqueId", UUID::class.java) ?: return contentException("playerUniqueId")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId) ?: return failure(NoSuchPlayerException("Player cannot be found"))
        cloudPlayer as CloudPlayer
        /*
        val oldConnectedServer = cloudPlayer.getConnectedServer()
        oldConnectedServer?.let {
            val oldClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(it)
            oldClient?.sendUnitQuery(PacketIORemoveCloudPlayer(cloudPlayer.getUniqueId()))
        }
         */
        val newService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName) ?: return failure(NoSuchServiceException("New service cannot be found"))
        val newServiceClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(newService) ?: return failure(NoSuchServiceException("New service is not connected to the manager"))
        return newServiceClient.sendUnitQuery(PacketIOUpdateCloudPlayer(cloudPlayer))
    }
}