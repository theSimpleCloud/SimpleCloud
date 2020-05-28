package eu.thesimplecloud.api.player


import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.AbstractCacheList
import eu.thesimplecloud.api.cachelist.ICacheObjectUpdater
import eu.thesimplecloud.api.event.player.*
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.HashMap

abstract class AbstractCloudPlayerManager : AbstractCacheList<ICloudPlayer>(spreadUpdates = false), ICloudPlayerManager {

    private val updater = object : ICacheObjectUpdater<ICloudPlayer> {
        override fun getIdentificationName(): String {
            return "player-cache"
        }

        override fun getCachedObjectByUpdateValue(value: ICloudPlayer): ICloudPlayer? {
            return getCachedCloudPlayer(value.getName())
        }

        override fun determineEventsToCall(updateValue: ICloudPlayer, cachedValue: ICloudPlayer?): List<IEvent> {
            val events = ArrayList<IEvent>()
            val playerToUse = cachedValue ?: updateValue
            events.add(CloudPlayerUpdatedEvent(playerToUse))
            if (cachedValue == null) {
                events.add(CloudPlayerRegisteredEvent(playerToUse))
                return events
            }

            if (updateValue.getConnectedServerName() != null && updateValue.getConnectedServerName() != cachedValue.getConnectedServerName()) {
                val oldServer = cachedValue.getConnectedServer()
                events.add(CloudPlayerServerConnectEvent(playerToUse, oldServer, updateValue.getConnectedServer()!!))
            }
            if (cachedValue.getServerConnectState() == PlayerServerConnectState.CONNECTING && updateValue.getServerConnectState() == PlayerServerConnectState.CONNECTED) {
                events.add(CloudPlayerServerConnectedEvent(playerToUse, updateValue.getConnectedServer()!!))
            }

            return events
        }

        override fun mergeUpdateValue(updateValue: ICloudPlayer, cachedValue: ICloudPlayer) {
            cachedValue as CloudPlayer
            cachedValue.setConnectedProxyName(updateValue.getConnectedProxyName())
            cachedValue.setConnectedServerName(updateValue.getConnectedServerName())
            cachedValue.propertyMap = HashMap(updateValue.getProperties())
            cachedValue.setServerConnectState(updateValue.getServerConnectState())
        }

        override fun addNewValue(value: ICloudPlayer) {
            values.add(value)
        }

    }

    override fun getUpdater(): ICacheObjectUpdater<ICloudPlayer> {
        return this.updater
    }

    override fun delete(value: ICloudPlayer, fromPacket: Boolean) {
        super<AbstractCacheList>.delete(value, fromPacket)
        CloudAPI.instance.getEventManager().call(CloudPlayerUnregisteredEvent(value))
    }

    /**
     * Creates a [ICommunicationPromise] with the [cloudPlayer]
     * If the [cloudPlayer] is not null it will returns a promise completed with the player.
     * If the [cloudPlayer] is null it will return a promise failed with [NoSuchElementException]
     */
    fun promiseOfNullablePlayer(cloudPlayer: ICloudPlayer?): ICommunicationPromise<ICloudPlayer> {
        return CommunicationPromise.ofNullable(cloudPlayer, NoSuchElementException("CloudPlayer not found."))
    }


}