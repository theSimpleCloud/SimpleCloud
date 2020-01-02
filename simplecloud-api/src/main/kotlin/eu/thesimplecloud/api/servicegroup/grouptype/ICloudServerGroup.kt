package eu.thesimplecloud.api.servicegroup.grouptype

import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

interface ICloudServerGroup : ICloudServiceGroup {

    /**
     * Returns the names of proxy groups this group is hidden on
     */
    fun getHiddenAtProxyGroups(): List<String>

    override fun getServiceType(): ServiceType = ServiceType.SERVER

}