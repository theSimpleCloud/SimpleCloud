package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.screen.ICommandExecutable

class PacketInScreenMessage : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return contentException("cloudClientType")
        val name = this.jsonData.getString("name") ?: return contentException("name")
        val message = this.jsonData.getString("message") ?: return contentException("message")

        val commandExecutable: ICommandExecutable? = when (cloudClientType) {
            CloudClientType.SERVICE -> CloudLib.instance.getCloudServiceManger().getCloudService(name)
            CloudClientType.WRAPPER -> CloudLib.instance.getWrapperManager().getWrapperByName(name)
        }
        commandExecutable ?: return failure(NoSuchElementException("Cannot find service / wrapper by name: $name"))

        val screenManager = Launcher.instance.screenManager
        screenManager.addScreenMessage(commandExecutable, "[$name]$message")
        return unit()
    }
}