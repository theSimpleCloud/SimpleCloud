package eu.thesimplecloud.api.player


import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerChangedServerEvent
import eu.thesimplecloud.api.event.player.CloudPlayerRegisteredEvent
import eu.thesimplecloud.api.event.player.CloudPlayerUnregisteredEvent
import eu.thesimplecloud.api.event.player.CloudPlayerUpdatedEvent
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.NoSuchElementException

abstract class AbstractCloudPlayerManager : ICloudPlayerManager {

    private val cachedPlayers = CopyOnWriteArrayList<ICloudPlayer>()

    override fun updateCloudPlayer(cloudPlayer: ICloudPlayer, fromPacket: Boolean) {
        val cachedCloudPlayer = getCachedCloudPlayer(cloudPlayer.getUniqueId())
        if (cachedCloudPlayer == null) {
            cachedPlayers.add(cloudPlayer)
            CloudAPI.instance.getEventManager().call(CloudPlayerRegisteredEvent(cloudPlayer))
            CloudAPI.instance.getEventManager().call(CloudPlayerUpdatedEvent(cloudPlayer))
            return
        }
        val oldServerName = cachedCloudPlayer.getConnectedServerName()
        cachedCloudPlayer as CloudPlayer
        cachedCloudPlayer.setConnectedProxyName(cloudPlayer.getConnectedProxyName())
        cachedCloudPlayer.setConnectedServerName(cloudPlayer.getConnectedServerName())
        CloudAPI.instance.getEventManager().call(CloudPlayerUpdatedEvent(cachedCloudPlayer))

        if (cachedCloudPlayer.getConnectedServerName() != null && cachedCloudPlayer.getConnectedServerName() != oldServerName) {
            val oldServer = oldServerName?.let { CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(it) }
            CloudAPI.instance.getEventManager().call(CloudPlayerChangedServerEvent(cachedCloudPlayer, oldServer, cachedCloudPlayer.getConnectedServer()!!))
        }
    }

    override fun removeCloudPlayer(cloudPlayer: ICloudPlayer) {
        this.cachedPlayers.removeIf { it.getUniqueId() == cloudPlayer.getUniqueId() }
        CloudAPI.instance.getEventManager().call(CloudPlayerUnregisteredEvent(cloudPlayer))
    }

    override fun getAllCachedCloudPlayers(): Collection<ICloudPlayer> = this.cachedPlayers

    /**
     * Creates a [ICommunicationPromise] with the [cloudPlayer]
     * If the [cloudPlayer] is not null it will returns a promise completed with the player.
     * If the [cloudPlayer] is null it will return a promise failed with [NoSuchElementException]
     */
    fun promiseOfNullablePlayer(cloudPlayer: ICloudPlayer?): ICommunicationPromise<ICloudPlayer> {
        return CommunicationPromise.ofNullable(cloudPlayer, NoSuchElementException("CloudPlayer not found."))
    }


}