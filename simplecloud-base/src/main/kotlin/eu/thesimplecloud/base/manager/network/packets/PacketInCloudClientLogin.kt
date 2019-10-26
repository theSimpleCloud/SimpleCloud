package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.network.packets.template.PacketIOUpdateTemplate
import eu.thesimplecloud.lib.network.packets.wrapper.PacketIOUpdateWrapperInfo

class PacketInCloudClientLogin() : JsonPacket() {

    override suspend fun handle(connection: IConnection): IPacket? {
        val host = connection.getHost()!!
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return ObjectPacket.getNewObjectPacketWithContent(false)
        connection as IConnectedClient<IConnectedClientValue>
        CloudLib.instance.getWrapperManager().getAllWrappers().forEach { connection.sendQuery(PacketIOUpdateWrapperInfo(it)) }
        CloudLib.instance.getTemplateManager().getAllTemplates().forEach { connection.sendQuery(PacketIOUpdateTemplate(it)) }
        CloudLib.instance.getCloudServiceGroupManager().getAllGroups().forEach { connection.sendQuery(PacketIOUpdateCloudServiceGroup(it)) }
        CloudLib.instance.getCloudServiceManger().getAllCloudServices().forEach { connection.sendQuery(PacketIOUpdateCloudService(it)) }
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
                connection.sendQuery(PacketOutSetWrapperName(wrapperInfo.getName()))
                wrapperInfo.setAuthenticated(true)
                Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateWrapperInfo())
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper ${wrapperInfo.getName()} logged in.")
            }
        }

        return ObjectPacket.getNewObjectPacketWithContent(true)
    }
}