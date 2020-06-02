package eu.thesimplecloud.api.network.packets.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.startconfiguration.IServiceStartConfiguration
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOStartCloudService(): ObjectPacket<IServiceStartConfiguration>() {

    constructor(configuration: IServiceStartConfiguration) :this() {
        this.value = configuration
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<String> {
        val value = this.value ?: return contentException("value")
        val registeredPromise = CloudAPI.instance.getCloudServiceGroupManager().startNewService(value)
        return registeredPromise.then { it.getName() }
    }
}