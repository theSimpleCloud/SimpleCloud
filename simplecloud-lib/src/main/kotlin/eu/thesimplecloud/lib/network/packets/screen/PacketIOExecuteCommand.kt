package eu.thesimplecloud.lib.network.packets.screen

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.client.CloudClientType
import eu.thesimplecloud.lib.screen.ICommandExecutable

class PacketIOExecuteCommand() : JsonPacket() {

    constructor(cloudClientType: CloudClientType, serviceName: String, command: String) : this() {
        this.jsonData.append("cloudClientType", cloudClientType).append("serviceName", serviceName).append("command", command)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val cloudClientType = this.jsonData.getObject("cloudClientType", CloudClientType::class.java) ?: return contentException("cloudClientType")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val command = this.jsonData.getString("command") ?: return contentException("command")
        val commandExecutable: ICommandExecutable? = when(cloudClientType) {
            CloudClientType.WRAPPER -> {
                CloudLib.instance.getWrapperManager().getWrapperByName(serviceName)
            }
            CloudClientType.SERVICE -> {
                CloudLib.instance.getCloudServiceManger().getCloudServiceByName(serviceName)
            }
        }
        commandExecutable?.executeCommand(command)
        return unit()
    }
}