package eu.thesimplecloud.api.location

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService


class ServiceLocation(
        val serviceName: String,
        worldName: String,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float
) : SimpleLocation(worldName, x, y, z, yaw, pitch) {

    constructor(service: ICloudService, worldName: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float): this(service.getName(), worldName, x, y, z, yaw, pitch)

    val groupName = serviceName.split("-").dropLast(1).joinToString("-")

    constructor(service: ICloudService, worldName: String, x: Double, y: Double, z: Double) : this(service, worldName, x, y, z, 0F, 0F)

    /**
     * Returns this location converted to a [GroupLocation]
     */
    fun toGroupLocation() = GroupLocation(groupName, worldName, x, y, z)

    /**
     * Returns the service this location is on or null if the service is closed.
     */
    fun getService() = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(serviceName)

    override fun add(x: Double, y: Double, z: Double): ServiceLocation {
        val newSimpleLoc = super.add(x, y, z)
        return ServiceLocation(serviceName, worldName, newSimpleLoc.x, newSimpleLoc.y, newSimpleLoc.z, newSimpleLoc.yaw, newSimpleLoc.pitch)
    }

}