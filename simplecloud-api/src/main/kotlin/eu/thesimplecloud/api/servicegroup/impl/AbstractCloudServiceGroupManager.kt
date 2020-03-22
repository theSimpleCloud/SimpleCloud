package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupManager
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractCloudServiceGroupManager : ICloudServiceGroupManager {

    private val serviceGroups = CopyOnWriteArrayList<ICloudServiceGroup>()

    override fun updateGroup(cloudServiceGroup: ICloudServiceGroup, fromPacket: Boolean) {
        val cachedGroup = getServiceGroupByName(cloudServiceGroup.getName())
        if (cachedGroup == null) {
            this.serviceGroups.add(cloudServiceGroup)
            return
        }
        cachedGroup.setMaintenance(cloudServiceGroup.isInMaintenance())
        cachedGroup.setMaxMemory(cloudServiceGroup.getMaxMemory())
        cachedGroup.setMaxPlayers(cloudServiceGroup.getMaxPlayers())
        cachedGroup.setMaximumOnlineServiceCount(cloudServiceGroup.getMaximumOnlineServiceCount())
        cachedGroup.setMinimumOnlineServiceCount(cloudServiceGroup.getMinimumOnlineServiceCount())
        cachedGroup.setPercentToStartNewService(cloudServiceGroup.getPercentToStartNewService())
        cachedGroup.setTemplateName(cloudServiceGroup.getTemplateName())
    }

    protected fun removeGroup(cloudServiceGroup: ICloudServiceGroup){
        this.serviceGroups.remove(cloudServiceGroup)
    }

    override fun getAllGroups(): Collection<ICloudServiceGroup> = this.serviceGroups

    override fun clearCache() {
        this.serviceGroups.clear()
    }


}