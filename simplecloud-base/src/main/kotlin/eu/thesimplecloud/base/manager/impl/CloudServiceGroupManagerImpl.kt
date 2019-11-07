package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIORemoveCloudServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {


    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup> {
        val promise = Manager.instance.communicationServer.newPromise<ICloudServiceGroup>()
        if (getServiceGroup(cloudServiceGroup.getName()) == null) {
            updateGroup(cloudServiceGroup)
            promise.trySuccess(cloudServiceGroup)
        } else {
            promise.setFailure(IllegalArgumentException("Name of the specified group is already in use."))
        }
        return promise
    }

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        super.updateGroup(cloudServiceGroup)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateCloudServiceGroup(cloudServiceGroup))
        Manager.instance.cloudServiceGroupFileHandler.save(cloudServiceGroup)
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        val services = Manager.instance.serviceHandler.startServicesByGroup(cloudServiceGroup)
        return Manager.instance.communicationServer.newSucceededPromise(services.first())
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        if (CloudLib.instance.getCloudServiceManger().getCloudServicesByGroupName(cloudServiceGroup.getName()).isNotEmpty())
            return Manager.instance.communicationServer.newFailedUnitPromise(IllegalStateException("Can not delete CloudServiceGroup while services of this group are registered."))
        this.removeGroup(cloudServiceGroup)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIORemoveCloudServiceGroup(cloudServiceGroup.getName()))
        Manager.instance.cloudServiceGroupFileHandler.delete(cloudServiceGroup)
        return Manager.instance.communicationServer.newSucceededUnitPromise()
    }

}