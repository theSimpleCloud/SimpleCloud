package eu.thesimplecloud.base.manager.network.packets.player

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.CloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.TimeUnit

class PacketInCreateCloudPlayer() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val playerConnection = this.jsonData.getObject("playerConnection", DefaultPlayerConnection::class.java)
                ?: return contentException("playerConnection")
        val proxyName = this.jsonData.getString("proxyName") ?: return contentException("proxyName")
        val offlinePlayer = Manager.instance.offlineCloudPlayerLoader.getOfflinePlayer(playerConnection.getUniqueId())
        val cloudPlayer = if (offlinePlayer == null) {
            CloudPlayer(playerConnection.getName(), playerConnection.getUniqueId(), System.currentTimeMillis(), System.currentTimeMillis(), 0L, proxyName, null, playerConnection)
        } else {
            CloudPlayer(playerConnection.getName(), playerConnection.getUniqueId(), offlinePlayer.getFirstLogin(), System.currentTimeMillis(), offlinePlayer.getOnlineTime(), proxyName, null, playerConnection)
        }
        CloudAPI.instance.getCloudPlayerManager().updateCloudPlayer(cloudPlayer)
        Manager.instance.offlineCloudPlayerLoader.saveCloudPlayer(cloudPlayer.toOfflinePlayer() as OfflineCloudPlayer)

        Launcher.instance.scheduler.schedule({
            cloudPlayer.sendMessage("§aCloud §8> §7Das ist ein Test")
        }, 1, TimeUnit.SECONDS)
        Launcher.instance.scheduler.schedule({
            cloudPlayer.forceCommandExecution("op")
        }, 3, TimeUnit.SECONDS)
        Launcher.instance.scheduler.schedule({
            cloudPlayer.sendActionBar("§aEine Actionbar")
        }, 5, TimeUnit.SECONDS)
        Launcher.instance.scheduler.schedule({
            cloudPlayer.sendTitle("§aCloud §8>", "§7Der sub title", 5, 40, 2)
        }, 7, TimeUnit.SECONDS)
        Launcher.instance.scheduler.schedule({
            println("waiting loc")
            cloudPlayer.getLocation().then {
                println("location received.")
                cloudPlayer.teleport(it.add(0.0, 1.5, 0.0)).addFailureListener { println("failure: " + it.message)}
            }.addFailureListener { println("failure 2: " + it.message) }
        }, 9, TimeUnit.SECONDS)
        Launcher.instance.scheduler.schedule({
            cloudPlayer.kick("§aCloud §8> §7Du wurdest gekicket.")
        }, 14, TimeUnit.SECONDS)
        return unit()
    }
}