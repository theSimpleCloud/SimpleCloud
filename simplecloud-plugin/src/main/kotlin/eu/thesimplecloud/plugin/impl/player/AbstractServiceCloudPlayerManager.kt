package eu.thesimplecloud.plugin.impl.player

import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.SimpleCloudPlayer
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.util.*
import java.util.function.Predicate

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:52
 */
abstract class AbstractServiceCloudPlayerManager: AbstractCloudPlayerManager() {

    override fun updateCloudPlayer(cloudPlayer: ICloudPlayer, fromPacket: Boolean) {
        super.updateCloudPlayer(cloudPlayer, fromPacket)
        if (!fromPacket)
            CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudPlayer(cloudPlayer))
    }

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(uniqueId)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(uniqueId))
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(name)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(name))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOSetCloudPlayerUpdates(cloudPlayer, update, serviceName))
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(name))
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOfflinePlayer(uniqueId))
    }

    override fun getOnlinePlayersFiltered(predicate: Predicate<ICloudPlayer>): ICommunicationPromise<List<SimpleCloudPlayer>> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetOnlinePlayersFiltered(predicate))
    }

}