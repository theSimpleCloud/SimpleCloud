package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.screen.ICommandExecutable

class PacketInScreenMessage : JsonPacket() {

    override suspend fun handle(connection: IConnection): IPacket? {
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return null
        val name = this.jsonData.getString("name") ?: return null
        val message = this.jsonData.getString("message") ?: return null

        val commandExecutable: ICommandExecutable? = when (cloudClientType) {
            CloudClientType.SERVICE -> CloudLib.instance.getCloudServiceManger().getCloudService(name)
            CloudClientType.WRAPPER -> CloudLib.instance.getWrapperManager().getWrapperByName(name)
        }
        commandExecutable ?: return null

        val screenManager = Launcher.instance.screenManager
        screenManager.addScreenMessage(commandExecutable, "[$name]$message")
        return null
    }
}