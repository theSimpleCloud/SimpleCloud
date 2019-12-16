package eu.thesimplecloud.lib.location

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ICloudService


class ServiceLocation(
        service: ICloudService,
        worldName: String,
        x: Double,
        y: Double,
        z: Double
) : SimpleLocation(worldName, x, y, z) {

    val serviceName = service.getName()
    val groupName = service.getGroupName()

    /**
     * Returns this location converted to a [GroupLocation]
     */
    fun toGroupLocation() = GroupLocation(groupName, worldName, x, y, z)

    /**
     * Returns the service this location is on or null if the service is closed.
     */
    fun getService() = CloudLib.instance.getCloudServiceManger().getCloudService(serviceName)

}