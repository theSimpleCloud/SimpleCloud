package eu.thesimplecloud.lib.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

class PacketIOUpdateCloudServiceGroup : PacketIOCloudServiceGroupData {

    constructor(cloudServiceGroup: ICloudServiceGroup): super(cloudServiceGroup)
    constructor(): super()

    override fun handleData(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        CloudLib.instance.getCloudServiceGroupManager().updateGroup(cloudServiceGroup)
        return unit()
    }


}