package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
    }

    override fun updateToNetwork(cloudService: ICloudService) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(cloudService))
    }
}