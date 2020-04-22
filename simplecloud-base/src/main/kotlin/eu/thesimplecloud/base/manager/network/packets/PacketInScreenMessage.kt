package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher

class PacketInScreenMessage : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return contentException("cloudClientType")
        val name = this.jsonData.getString("name") ?: return contentException("name")
        val message = this.jsonData.getString("message") ?: return contentException("message")

        val commandExecutable: ICommandExecutable? = when (cloudClientType) {
            CloudClientType.SERVICE -> CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(name)
            CloudClientType.WRAPPER -> CloudAPI.instance.getWrapperManager().getWrapperByName(name)?.obj
        }
        commandExecutable ?: return failure(NoSuchElementException("Cannot find service / wrapper by name: $name"))

        val screenManager = Launcher.instance.screenManager
        screenManager.addScreenMessage(commandExecutable, "[$name]$message")
        return unit()
    }
}