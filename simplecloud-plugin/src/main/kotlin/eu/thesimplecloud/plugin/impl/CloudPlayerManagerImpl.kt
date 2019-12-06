package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.lib.network.packets.player.*
import eu.thesimplecloud.lib.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.lib.player.AbstractCloudPlayerManager
import eu.thesimplecloud.lib.player.CloudPlayer
import eu.thesimplecloud.lib.player.ICloudPlayer
import eu.thesimplecloud.lib.player.text.CloudText
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.plugin.proxy.text.CloudTextBuilder
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(uniqueId)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(uniqueId), ObjectPacketResponseHandler(CloudPlayer::class.java)) as ICommunicationPromise<ICloudPlayer>
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        val cachedCloudPlayer = getCachedCloudPlayer(name)
        if (cachedCloudPlayer != null) {
            return CommunicationPromise.of(cachedCloudPlayer)
        }
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOGetCloudPlayer(name), ObjectPacketResponseHandler(CloudPlayer::class.java)) as ICommunicationPromise<ICloudPlayer>
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, cloudText: CloudText) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
                getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(CloudTextBuilder().build(cloudText))
                return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendMessageToCloudPlayer(cloudPlayer, cloudText))
    }

    override fun sendPlayerToService(cloudPlayer: ICloudPlayer, cloudService: ICloudService) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getServerInfoByCloudService(cloudService)?.let { getProxiedPlayerByCloudPlayer(cloudPlayer)?.connect(it) }
            return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendCloudPlayerToService(cloudPlayer, cloudService))
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.disconnect(CloudTextBuilder().build(CloudText(message)))
            return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
    }

    override fun sendTitle(cloudPlayer: ICloudPlayer, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            val titleObj = ProxyServer.getInstance().createTitle()
            titleObj.title(CloudTextBuilder().build(CloudText(title)))
                    .subTitle(CloudTextBuilder().build(CloudText(subTitle)))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendTitle(titleObj)
            return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendTitleToCloudPlayer(cloudPlayer, title, subTitle, fadeIn, stay, fadeOut))
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.chat("/$command")
            return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOCloudPlayerForceCommandExecution(cloudPlayer, command))
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        if (cloudPlayer.getConnectedProxyName() == CloudPlugin.instance.thisServiceName) {
            getProxiedPlayerByCloudPlayer(cloudPlayer)?.sendMessage(ChatMessageType.ACTION_BAR, CloudTextBuilder().build(CloudText(actionbar)))
            return
        }
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOSendActionbarToCloudPlayer(cloudPlayer, actionbar))
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        CloudPlugin.instance.communicationClient.sendQuery(PacketIOSetCloudPlayerUpdates(cloudPlayer, update, serviceName))
    }

    private fun getProxiedPlayerByCloudPlayer(cloudPlayer: ICloudPlayer): ProxiedPlayer? {
        return ProxyServer.getInstance().getPlayer(cloudPlayer.getUniqueId())
    }

    private fun getServerInfoByCloudService(cloudService: ICloudService): ServerInfo? {
        return ProxyServer.getInstance().getServerInfo(cloudService.getName())
    }
}