package eu.thesimplecloud.api.player.connection

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.player.CloudPlayerServerConnectedEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.PlayerServerConnectState
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

class ConnectionResponse(val playerUniqueId: UUID, val alreadyConnected: Boolean) {

    fun getCloudPlayer(): ICloudPlayer {
        return CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(playerUniqueId)
                ?: throw IllegalStateException("Unable to find player by uuid $playerUniqueId")
    }

    fun createConnectedPromise(): ICommunicationPromise<CloudPlayerServerConnectedEvent> {
        val cloudPlayer = this.getCloudPlayer()
        if (alreadyConnected || cloudPlayer.getServerConnectState() == PlayerServerConnectState.CONNECTED)
            return CommunicationPromise.of(CloudPlayerServerConnectedEvent(cloudPlayer, cloudPlayer.getConnectedServer()!!))
        return cloudListener<CloudPlayerServerConnectedEvent>()
                .addCondition { it.cloudPlayer === cloudPlayer }
                .toPromise()
    }

}