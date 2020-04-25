package eu.thesimplecloud.base.manager.player

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.base.manager.impl.CloudAPIImpl
import eu.thesimplecloud.base.manager.network.packets.player.PacketOutGetPlayerOnlineStatus
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 10.04.2020
 * Time: 21:35
 */
class PlayerUnregisterScheduler {

    fun startScheduler() {
        Launcher.instance.scheduler.scheduleAtFixedRate({
            CloudAPI.instance.getCloudPlayerManager().getAllCachedCloudPlayers().forEach {player ->

                checkPlayerOnlineStatus(player).then {
                    if (!it) {
                        CloudAPI.instance.getCloudPlayerManager().removeCloudPlayer(player)
                    }
                }

            }
        },30L, 30, TimeUnit.SECONDS)
    }

    private fun checkPlayerOnlineStatus(cloudPlayer: ICloudPlayer): ICommunicationPromise<Boolean> {
        val connectedProxy = cloudPlayer.getConnectedProxy() ?: return CommunicationPromise.of(false)

        val client = Manager.instance.communicationServer.getClientManager().getClientByClientValue(connectedProxy)
                ?: return CommunicationPromise.of(false)

        return client.sendQuery(PacketOutGetPlayerOnlineStatus(cloudPlayer.getUniqueId()))
    }

}