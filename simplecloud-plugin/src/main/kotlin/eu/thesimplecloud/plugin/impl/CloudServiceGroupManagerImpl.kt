package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.network.packets.service.PacketIOStartCloudService
import eu.thesimplecloud.api.network.packets.servicegroup.PacketIOCreateServiceGroup
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.startconfiguration.IServiceStartConfiguration
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.impl.AbstractCloudServiceGroupManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {

    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        return CloudPlugin.instance.communicationClient.sendQuery(PacketIOCreateServiceGroup(cloudServiceGroup))
    }

    override fun startNewService(serviceStartConfiguration: IServiceStartConfiguration): ICommunicationPromise<ICloudService> {
        val namePromise = CloudPlugin.instance.communicationClient.sendQuery<String>(PacketIOStartCloudService(serviceStartConfiguration))
        return namePromise.then { CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(it)!! }
    }

}