package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.ConnectionPromise
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.impl.AbstractCloudServiceGroupManager

class CloudServiceGroupManagerImpl : AbstractCloudServiceGroupManager() {

    override fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudServiceGroup> {

    }

    override fun startNewService(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<ICloudService> {
    }

    override fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): IConnectionPromise<Unit> {
    }

}