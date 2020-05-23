package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentType
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
        val cloudClientType = this.jsonData.getObject("cloudClientType", NetworkComponentType::class.java)
                ?: return contentException("cloudClientType")
        connection as IConnectedClient<IConnectedClientValue>
        CloudAPI.instance.getWrapperManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getCloudServiceManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getTemplateManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        CloudAPI.instance.getCloudServiceGroupManager().sendAllCachedObjectsToConnection(connection).awaitCoroutine()
        when (cloudClientType) {
            NetworkComponentType.SERVICE -> {
                val name = this.jsonData.getString("name") ?: return contentException("name")
                val cloudService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
                        ?: return failure(NoSuchElementException("Service not found"))
                connection.setClientValue(cloudService)
                cloudService.setAuthenticated(true)
                CloudAPI.instance.getCloudServiceManager().update(cloudService)
                CloudAPI.instance.getCloudServiceManager().sendUpdateToConnection(cloudService, connection).awaitCoroutine()
                Launcher.instance.consoleSender.sendMessage("manager.login.service", "Service %SERVICE%", cloudService.getName(), " logged in.")
            }
            NetworkComponentType.WRAPPER -> {
                val wrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByHost(host)
                        ?: return failure(NoSuchElementException("Wrapper not found"))
                connection.setClientValue(wrapperInfo)
                wrapperInfo.setAuthenticated(true)
                CloudAPI.instance.getWrapperManager().update(wrapperInfo)
                CloudAPI.instance.getWrapperManager().sendUpdateToConnection(wrapperInfo, connection).awaitCoroutine()
                connection.sendUnitQuery(PacketOutSetWrapperName(wrapperInfo.getName())).awaitCoroutine()
                Launcher.instance.consoleSender.sendMessage("manager.login.wrapper", "Wrapper %WRAPPER%", wrapperInfo.getName(), " logged in.")
            }
        }

        return unit()
    }
}