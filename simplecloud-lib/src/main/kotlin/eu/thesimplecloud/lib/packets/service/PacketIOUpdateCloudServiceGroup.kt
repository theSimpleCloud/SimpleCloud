package eu.thesimplecloud.lib.packets.service

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultServerGroup

class PacketIOUpdateCloudServiceGroup() : JsonPacket() {

    constructor(cloudServiceGroup: ICloudServiceGroup): this() {
        this.jsonData.append("serviceType", cloudServiceGroup.getServiceType()).append("group", cloudServiceGroup)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val serviceType = this.jsonData.getObject("serviceType", ServiceType::class.java) ?: return null
        val serviceGroupClass = when(serviceType){
            ServiceType.LOBBY -> DefaultLobbyGroup::class.java
            ServiceType.SERVER -> DefaultServerGroup::class.java
            ServiceType.PROXY -> DefaultProxyGroup::class.java
        }
        val serviceGroup = this.jsonData.getObject("group", serviceGroupClass) ?: return null
        CloudLib.instance.getCloudServiceGroupManager().updateGroup(serviceGroup)
        return null
    }
}