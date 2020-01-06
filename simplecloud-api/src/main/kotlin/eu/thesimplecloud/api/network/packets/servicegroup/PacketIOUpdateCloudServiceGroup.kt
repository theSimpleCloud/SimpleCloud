package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

class PacketIOUpdateCloudServiceGroup : PacketIOCloudServiceGroupData {

    constructor(cloudServiceGroup: ICloudServiceGroup): super(cloudServiceGroup)
    constructor(): super()

    override fun handleData(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        CloudAPI.instance.getCloudServiceGroupManager().updateGroup(cloudServiceGroup, fromPacket = true)
        return unit()
    }


}