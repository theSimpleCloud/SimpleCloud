package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.packets.service.PacketIORemoveCloudServiceGroup
import eu.thesimplecloud.lib.packets.service.PacketIOUpdateCloudServiceGroup
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager
import java.lang.IllegalStateException

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {



    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudServiceGroup> {
        val promise = Manager.instance.nettyServer.newPromise<ICloudServiceGroup>()
        updateGroup(cloudServiceGroup)
        promise.trySuccess(cloudServiceGroup)
        return promise
    }

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        super.updateGroup(cloudServiceGroup)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIOUpdateCloudServiceGroup(cloudServiceGroup))
        Manager.instance.cloudServiceGroupFileHandler.saveGroup(cloudServiceGroup)
    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudService> {
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<Unit> {
        check(CloudLib.instance.getCloudServiceManger().getCloudServicesByGroupName(cloudServiceGroup.getName()).isEmpty()) { "Can not delete CloudServiceGroup while services of this group are registered." }
        this.removeGroup(cloudServiceGroup)
        Manager.instance.nettyServer.getClientManager().sendPacketToAllClients(PacketIORemoveCloudServiceGroup(cloudServiceGroup.getName()))
        return Manager.instance.nettyServer.newSucceededPromise(Unit)
    }

}