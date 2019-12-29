package eu.thesimplecloud.plugin.impl

import eu.thesimplecloud.lib.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService) {
        CloudPlugin.instance.communicationClient.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
    }
}