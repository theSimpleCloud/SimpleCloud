package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

abstract class PacketIOCloudServiceGroupData() : JsonPacket() {

    constructor(cloudServiceGroup: ICloudServiceGroup): this() {
        this.jsonData.append("serviceType", cloudServiceGroup.getServiceType()).append("group", cloudServiceGroup)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val serviceType = this.jsonData.getObject("serviceType", ServiceType::class.java) ?: return contentException("serviceType")
        val serviceGroupClass = when(serviceType){
            ServiceType.LOBBY -> DefaultLobbyGroup::class.java
            ServiceType.SERVER -> DefaultServerGroup::class.java
            ServiceType.PROXY -> DefaultProxyGroup::class.java
        }
        val serviceGroup = this.jsonData.getObject("group", serviceGroupClass) ?: return contentException("group")
        return handleData(serviceGroup)
    }

    abstract fun handleData(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<out Any>

}