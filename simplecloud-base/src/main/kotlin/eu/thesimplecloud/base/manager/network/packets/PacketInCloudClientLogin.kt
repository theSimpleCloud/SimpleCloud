package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.packets.service.PacketIORemoveCloudServiceGroup
import eu.thesimplecloud.lib.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.lib.packets.service.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.packets.template.PacketIOAddTemplate
import eu.thesimplecloud.lib.packets.wrapper.PacketIOWrapperInfo

class PacketInCloudClientLogin() : JsonPacket() {

    override suspend fun handle(connection: IConnection): IPacket? {
        println("login received")
        val host = connection.getHost()!!
        println(this.jsonData.getAsJsonString())
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        connection as IConnectedClient<IConnectedClientValue>
        when (cloudClientType) {
            CloudClientType.SERVICE -> {
                val name = this.jsonData.getString("name") ?: return ObjectPacket.getNewObjectPacketWithContent(false)
                val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(name) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
                connection.setClientValue(cloudService)
                cloudService.setAuthenticated(true)
                Launcher.instance.consoleSender.sendMessage("manager.login.service", "Service ${cloudService.getName()} logged in.")
            }
            CloudClientType.WRAPPER -> {
                val wrapperInfo = CloudLib.instance.getWrapperManager().getWrapperByHost(host) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
                connection.setClientValue(wrapperInfo)
                wrapperInfo.setAuthenticated(true)
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper ${wrapperInfo.getName()} logged in.")
            }
        }
        CloudLib.instance.getWrapperManager().getAllWrappers().forEach { connection.sendQuery(PacketIOWrapperInfo(it)) }
        CloudLib.instance.getTemplateManager().getAllTemplates().forEach { connection.sendQuery(PacketIOAddTemplate(it)) }
        CloudLib.instance.getCloudServiceGroupManager().getAllGroups().forEach { connection.sendQuery(PacketIOUpdateCloudServiceGroup(it)) }
        CloudLib.instance.getCloudServiceManger().getAllCloudServices().forEach { connection.sendQuery(PacketIOUpdateCloudService(it)) }
        return ObjectPacket.getNewObjectPacketWithContent(true)
    }
}