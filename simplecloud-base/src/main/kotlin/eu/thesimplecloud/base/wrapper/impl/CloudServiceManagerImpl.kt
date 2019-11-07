package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.process.CloudServiceProcess
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.impl.AbstractCloudServiceManager
import java.lang.IllegalStateException

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun stopService(cloudService: ICloudService) {
        if (cloudService.getWrapperName() == Wrapper.instance.thisWrapperName) {
            val cloudServiceProcess = Wrapper.instance.cloudServiceProcessManager.getCloudServiceProcessByServiceName(cloudService.getName())
            cloudServiceProcess?.shutdown()
        } else {
            Wrapper.instance.communicationClient.sendQuery(PacketIOStopCloudService(cloudService.getName()))
        }
    }

    override fun startService(cloudService: ICloudService) {
        val processQueue = Wrapper.instance.processQueue
        checkNotNull(processQueue) { "Process Queue was null while trying to add a service to the queue." }
        processQueue.addToQueue(cloudService)
    }

}