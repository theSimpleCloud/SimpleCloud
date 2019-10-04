package eu.thesimplecloud.lib.servicegroup.grouptype

import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

interface ICloudServerGroup : ICloudServiceGroup {

    /**
     * Returns the names of proxy groups this group is hidden on
     */
    fun getHiddenAtProxyGroups(): List<String>

}