package eu.thesimplecloud.lib.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

class PacketIOCreateServiceGroup : PacketIOCloudServiceGroupData {

    constructor(cloudServiceGroup: ICloudServiceGroup) : super(cloudServiceGroup)
    constructor() : super()

    override fun handleData(cloudServiceGroup: ICloudServiceGroup): IPacket? {
        return ObjectPacket.getNewObjectPacketWithContent(CloudLib.instance.getCloudServiceGroupManager().createServiceGroup(cloudServiceGroup).isSuccess)
    }
}