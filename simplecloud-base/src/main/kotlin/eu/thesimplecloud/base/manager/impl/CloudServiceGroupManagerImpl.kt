package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIORemoveCloudServiceGroup
import eu.thesimplecloud.lib.network.packets.servicegroup.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {


    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        val promise = CommunicationPromise<Unit>()
        if (getServiceGroupByName(cloudServiceGroup.getName()) == null) {
            updateGroup(cloudServiceGroup)
            promise.trySuccess(Unit)
        } else {
            promise.setFailure(IllegalArgumentException("Name of the specified group is already registered."))
        }
        return promise
    }

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        super.updateGroup(cloudServiceGroup)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIOUpdateCloudServiceGroup(cloudServiceGroup))
        Manager.instance.cloudServiceGroupFileHandler.save(cloudServiceGroup)
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        if (CloudLib.instance.getCloudServiceGroupManager().getServiceGroupByName(cloudServiceGroup.getName()) == null)
            return CommunicationPromise.failed(NoSuchElementException("Group is not registered."))
        val services = Manager.instance.serviceHandler.startServicesByGroup(cloudServiceGroup)
        return CommunicationPromise.of(services.first())
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit> {
        if (CloudLib.instance.getCloudServiceManger().getCloudServicesByGroupName(cloudServiceGroup.getName()).isNotEmpty())
            return CommunicationPromise.failed(IllegalStateException("Cannot delete service group while services of this group are registered."))
        this.removeGroup(cloudServiceGroup)
        Manager.instance.communicationServer.getClientManager().sendPacketToAllClients(PacketIORemoveCloudServiceGroup(cloudServiceGroup.getName()))
        Manager.instance.cloudServiceGroupFileHandler.delete(cloudServiceGroup)
        return CommunicationPromise.of(Unit)
    }

}