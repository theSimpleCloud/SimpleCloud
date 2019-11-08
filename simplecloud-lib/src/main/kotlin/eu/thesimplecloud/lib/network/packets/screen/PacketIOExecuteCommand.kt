package eu.thesimplecloud.lib.network.packets.screen

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.screen.ICommandExecutable

class PacketIOExecuteCommand() : JsonPacket() {

    constructor(cloudClientType: CloudClientType, serviceName: String, command: String) : this() {
        this.jsonData.append("cloudClientType", cloudClientType).append("serviceName", serviceName).append("command", command)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return null
        val serviceName = this.jsonData.getString("serviceName") ?: return null
        val command = this.jsonData.getString("command") ?: return null
        val commandExecutable: ICommandExecutable? = when(cloudClientType) {
            CloudClientType.WRAPPER -> {
                CloudLib.instance.getWrapperManager().getWrapperByName(serviceName)
            }
            CloudClientType.SERVICE -> {
                CloudLib.instance.getCloudServiceManger().getCloudService(serviceName)
            }
        }
        commandExecutable?.executeCommand(command)
        return null
    }
}