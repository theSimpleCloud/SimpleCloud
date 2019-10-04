package eu.thesimplecloud.lib.servicegroup.grouptype

import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

interface ICloudProxyGroup : ICloudServiceGroup {

    /**
     * Returns the start port of this proxy group.
     * All services started by this group will be started on this port or if it is already in use the port will be counted up
     */
    fun getStartPort(): Int
}