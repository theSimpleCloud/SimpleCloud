package eu.thesimplecloud.lib.servicegroup.impl

import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager

abstract class AbstractCloudServiceGroupManager : ICloudServiceGroupManager {

    private val serviceGroups = ArrayList<ICloudServiceGroup>()

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        val cashedGroup = getServiceGroup(cloudServiceGroup.getName())
        if (cashedGroup == null) {
            this.serviceGroups.add(cloudServiceGroup)
            return
        }
        cashedGroup.setMaintenance(cloudServiceGroup.isInMaintenance())
        cashedGroup.setMaxMemory(cloudServiceGroup.getMaxMemory())
        cashedGroup.setMaxPlayers(cloudServiceGroup.getMaxPlayers())
        cashedGroup.setMaximumOnlineServiceCount(cloudServiceGroup.getMaximumOnlineServiceCount())
        cashedGroup.setMinimumOnlineServiceCount(cloudServiceGroup.getMinimumOnlineServiceCount())
        cashedGroup.setModuleNamesToCopy(cloudServiceGroup.getModuleNamesToCopy())
        cashedGroup.setPercentToStartNewService(cloudServiceGroup.getPercentToStartNewService())
        cashedGroup.setTemplateName(cloudServiceGroup.getTemplateName())
    }

    protected fun removeGroup(cloudServiceGroup: ICloudServiceGroup){
        this.serviceGroups.remove(cloudServiceGroup)
    }

    override fun getAllGroups(): List<ICloudServiceGroup> = this.serviceGroups

    override fun clearCache() {
        this.serviceGroups.clear()
    }


}