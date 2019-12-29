package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
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

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val host = connection.getHost()!!
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java)
                ?: return contentException("cloudClientType")
        connection as IConnectedClient<IConnectedClientValue>
        CloudLib.instance.getWrapperManager().getAllWrappers().forEach { connection.sendUnitQuery(PacketIOUpdateWrapperInfo(it)) }
        CloudLib.instance.getTemplateManager().getAllTemplates().forEach { connection.sendUnitQuery(PacketIOUpdateTemplate(it)) }
        CloudLib.instance.getCloudServiceGroupManager().getAllGroups().forEach { connection.sendUnitQuery(PacketIOUpdateCloudServiceGroup(it)) }
        CloudLib.instance.getCloudServiceManger().getAllCloudServices().forEach { connection.sendUnitQuery(PacketIOUpdateCloudService(it)) }
        when (cloudClientType) {
            CloudClientType.SERVICE -> {
                val name = this.jsonData.getString("name") ?: return contentException("name")
                val cloudService = CloudLib.instance.getCloudServiceManger().getCloudService(name)
                        ?: return failure(NoSuchElementException("Service not found"))
                connection.setClientValue(cloudService)
                cloudService.setAuthenticated(true)
                CloudLib.instance.getCloudServiceManger().updateCloudService(cloudService)
                Launcher.instance.consoleSender.sendMessage("manager.login.service", "Service %SERVICE%", cloudService.getName(), " logged in.")
            }
            CloudClientType.WRAPPER -> {
                val wrapperInfo = CloudLib.instance.getWrapperManager().getWrapperByHost(host)
                        ?: return failure(NoSuchElementException("Wrapper not found"))
                connection.setClientValue(wrapperInfo)
                wrapperInfo.setAuthenticated(true)
                CloudLib.instance.getWrapperManager().updateWrapper(wrapperInfo)
                connection.sendUnitQuery(PacketOutSetWrapperName(wrapperInfo.getName()))
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper %WRAPPER%", wrapperInfo.getName(), " logged in.")
            }
        }

        return unit()
    }
}