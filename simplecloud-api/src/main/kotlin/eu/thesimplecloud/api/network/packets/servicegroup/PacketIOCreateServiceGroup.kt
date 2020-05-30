package eu.thesimplecloud.api.network.packets.servicegroup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOCreateServiceGroup : PacketIOCloudServiceGroupData {

    constructor(cloudServiceGroup: ICloudServiceGroup) : super(cloudServiceGroup)
    constructor() : super()

    override fun handleData(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        return CloudAPI.instance.getCloudServiceGroupManager().createServiceGroup(cloudServiceGroup)
    }
}