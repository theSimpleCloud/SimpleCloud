package eu.thesimplecloud.api.network.packets.screen

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOExecuteCommand() : JsonPacket() {

    constructor(cloudClientType: NetworkComponentType, serviceName: String, command: String) : this() {
        this.jsonData.append("cloudClientType", cloudClientType).append("serviceName", serviceName).append("command", command)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val cloudClientType = this.jsonData.getObject("cloudClientType", NetworkComponentType::class.java) ?: return contentException("cloudClientType")
        val serviceName = this.jsonData.getString("serviceName") ?: return contentException("serviceName")
        val command = this.jsonData.getString("command") ?: return contentException("command")
        val commandExecutable: ICommandExecutable? = when(cloudClientType) {
            NetworkComponentType.WRAPPER -> {
                CloudAPI.instance.getWrapperManager().getWrapperByName(serviceName)
            }
            NetworkComponentType.SERVICE -> {
                CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
            }
            else -> {
                throw UnsupportedOperationException()
            }
        }
        commandExecutable?.executeCommand(command)
        return unit()
    }
}