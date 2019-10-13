package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.packets.service.PacketIORemoveCloudServiceGroup
import eu.thesimplecloud.lib.packets.service.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import java.lang.IllegalArgumentException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {



    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudServiceGroup> {
        val promise = Manager.instance.nettyServer.newPromise<ICloudServiceGroup>()
        if (getServiceGroup(cloudServiceGroup.getName()) == null){
            updateGroup(cloudServiceGroup)
            promise.trySuccess(cloudServiceGroup)
        } else {
            promise.setFailure(IllegalArgumentException("Name of the specified group is already in use."))
        }
        return promise
    }

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        super.updateGroup(cloudServiceGroup)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIOUpdateCloudServiceGroup(cloudServiceGroup))
        Manager.instance.cloudServiceGroupFileHandler.save(cloudServiceGroup)
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudService> = TODO()

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<Unit> {
        check(CloudLib.instance.getCloudServiceManger().getCloudServicesByGroupName(cloudServiceGroup.getName()).isEmpty()) { "Can not delete CloudServiceGroup while services of this group are registered." }
        this.removeGroup(cloudServiceGroup)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIORemoveCloudServiceGroup(cloudServiceGroup.getName()))
        Manager.instance.cloudServiceGroupFileHandler.delete(cloudServiceGroup)
        return Manager.instance.nettyServer.newSucceededPromise(Unit)
    }

}