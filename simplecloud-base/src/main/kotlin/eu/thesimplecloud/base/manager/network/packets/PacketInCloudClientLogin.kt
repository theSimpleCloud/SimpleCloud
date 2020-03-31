package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.network.packets.servicegroup.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.api.network.packets.template.PacketIOUpdateTemplate
import eu.thesimplecloud.api.network.packets.wrapper.PacketIOUpdateWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class PacketInCloudClientLogin() : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val host = connection.getHost()!!
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java)
                ?: return contentException("cloudClientType")
        connection as IConnectedClient<IConnectedClientValue>
        val wrapperPromises = CloudAPI.instance.getWrapperManager().getAllWrappers().map { connection.sendUnitQuery(PacketIOUpdateWrapperInfo(it)) }
        val templatePromises = CloudAPI.instance.getTemplateManager().getAllTemplates().map { connection.sendUnitQuery(PacketIOUpdateTemplate(it)) }
        val groupPromises = CloudAPI.instance.getCloudServiceGroupManager().getAllGroups().map { connection.sendUnitQuery(PacketIOUpdateCloudServiceGroup(it)) }
        val servicePromises = CloudAPI.instance.getCloudServiceManager().getAllCloudServices().map { connection.sendUnitQuery(PacketIOUpdateCloudService(it)) }
        wrapperPromises.union(templatePromises)
                .union(groupPromises)
                .union(servicePromises)
                .combineAllPromises()
                .awaitUninterruptibly()
        when (cloudClientType) {
            CloudClientType.SERVICE -> {
                val name = this.jsonData.getString("name") ?: return contentException("name")
                val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
                        ?: return failure(NoSuchElementException("Service not found"))
                connection.setClientValue(cloudService)
                cloudService.setAuthenticated(true)
                CloudAPI.instance.getCloudServiceManager().updateCloudService(cloudService)
                connection.sendUnitQuery(PacketIOUpdateCloudService(cloudService)).awaitUninterruptibly()
                Launcher.instance.consoleSender.sendMessage("manager.login.service", "Service %SERVICE%", cloudService.getName(), " logged in.")
            }
            CloudClientType.WRAPPER -> {
                val wrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByHost(host)
                        ?: return failure(NoSuchElementException("Wrapper not found"))
                connection.setClientValue(wrapperInfo)
                wrapperInfo.setAuthenticated(true)
                CloudAPI.instance.getWrapperManager().updateWrapper(wrapperInfo)
                connection.sendUnitQuery(PacketOutSetWrapperName(wrapperInfo.getName()))
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper %WRAPPER%", wrapperInfo.getName(), " logged in.")
            }
        }

        return unit()
    }
}