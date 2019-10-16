package eu.thesimplecloud.lib.network.reponsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.lib.service.ServiceType
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.lib.servicegroup.impl.DefaultServerGroup

class CloudServiceGroupResponseHandler : IPacketResponseHandler<ICloudServiceGroup> {
    override fun handleResponse(packet: IPacket): ICloudServiceGroup? {
        if (packet !is JsonPacket) return null
        val serviceType = packet.jsonData.getObject("serviceType", ServiceType::class.java) ?: return null
        val serviceGroupClass = when(serviceType){
            ServiceType.LOBBY -> DefaultLobbyGroup::class.java
            ServiceType.SERVER -> DefaultServerGroup::class.java
            ServiceType.PROXY -> DefaultProxyGroup::class.java
        }
        return packet.jsonData.getObject("group", serviceGroupClass) ?: return null
    }
}