package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractCloudServiceGroupManager : ICloudServiceGroupManager {

    private val serviceGroups = CopyOnWriteArrayList<ICloudServiceGroup>()

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup) {
        val cashedGroup = getServiceGroupByName(cloudServiceGroup.getName())
        if (cashedGroup == null) {
            this.serviceGroups.add(cloudServiceGroup)
            return
        }
        cashedGroup.setMaintenance(cloudServiceGroup.isInMaintenance())
        cashedGroup.setMaxMemory(cloudServiceGroup.getMaxMemory())
        cashedGroup.setMaxPlayers(cloudServiceGroup.getMaxPlayers())
        cashedGroup.setMaximumOnlineServiceCount(cloudServiceGroup.getMaximumOnlineServiceCount())
        cashedGroup.setMinimumOnlineServiceCount(cloudServiceGroup.getMinimumOnlineServiceCount())
        cashedGroup.setPercentToStartNewService(cloudServiceGroup.getPercentToStartNewService())
        cashedGroup.setTemplateName(cloudServiceGroup.getTemplateName())
    }

    protected fun removeGroup(cloudServiceGroup: ICloudServiceGroup){
        this.serviceGroups.remove(cloudServiceGroup)
    }

    override fun getAllGroups(): Collection<ICloudServiceGroup> = this.serviceGroups

    override fun clearCache() {
        this.serviceGroups.clear()
    }


}