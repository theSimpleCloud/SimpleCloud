package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.lib.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.AbstractCloudServiceManager

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService) {
        Wrapper.instance.communicationClient.sendQuery(PacketIOStopCloudService(cloudService.getName()))
    }
}