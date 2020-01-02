package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService) {
        if (cloudService.getWrapperName() == Wrapper.instance.thisWrapperName) {
            val cloudServiceProcess = Wrapper.instance.cloudServiceProcessManager.getCloudServiceProcessByServiceName(cloudService.getName())
            cloudServiceProcess?.shutdown()
        } else {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
        }
    }

    override fun startService(cloudService: ICloudService) {
        val processQueue = Wrapper.instance.processQueue
        checkNotNull(processQueue) { "Process-Queue was null while trying to add a service to the queue." }
        processQueue.addToQueue(cloudService)
    }

}