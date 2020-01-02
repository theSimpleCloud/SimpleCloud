package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.api.network.packets.service.PacketIOStopCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOWrapperStartService
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.AbstractCloudServiceManager
import java.lang.IllegalStateException

class CloudServiceManagerImpl : AbstractCloudServiceManager() {

    override fun updateCloudService(cloudService: ICloudService) {
        super.updateCloudService(cloudService)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateCloudService(cloudService))
    }

    override fun stopService(cloudService: ICloudService) {
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
        wrapperClient?.sendUnitQuery(PacketIOStopCloudService(cloudService.getName()))
    }

    override fun startService(cloudService: ICloudService) {
        if (cloudService.isActive() || cloudService.getState() == ServiceState.CLOSED) throw IllegalStateException("Can not start started service.")
        val wrapper = cloudService.getWrapper()
        val wrapperClient = Manager.instance.communicationServer.getClientManager().getClientByClientValue(wrapper)
        check(wrapperClient != null) { "Can not find client of wrapper to start service ${cloudService.getName()} on." }
        wrapperClient.sendUnitQuery(PacketIOWrapperStartService(cloudService.getName()))
    }
}