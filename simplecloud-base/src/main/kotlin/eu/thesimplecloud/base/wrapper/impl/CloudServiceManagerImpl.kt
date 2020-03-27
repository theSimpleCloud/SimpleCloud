package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun updateCloudService(cloudService: ICloudService, fromPacket: Boolean) {
        super.updateCloudService(cloudService, fromPacket)
        if (!fromPacket) Wrapper.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(cloudService))
    }

    override fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        if (cloudService.getWrapperName() == Wrapper.instance.thisWrapperName) {
            val cloudServiceProcess = Wrapper.instance.cloudServiceProcessManager.getCloudServiceProcessByServiceName(cloudService.getName())
            cloudServiceProcess?.shutdown()
        } else {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
        }
        return cloudService.closedPromise()
    }

    override fun startService(cloudService: ICloudService): ICommunicationPromise<Unit> {
        val processQueue = Wrapper.instance.processQueue
        checkNotNull(processQueue) { "Process-Queue was null while trying to add a service to the queue." }
        processQueue.addToQueue(cloudService)
        return cloudService.connectedPromise()
    }

}