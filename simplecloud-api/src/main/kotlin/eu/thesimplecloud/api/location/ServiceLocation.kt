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

    val groupName = serviceName.split("-").dropLast(1).joinToString("-")

    constructor(service: ICloudService, worldName: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float): this(service.getName(), worldName, x, y, z, yaw, pitch)

    constructor(service: ICloudService, worldName: String, x: Double, y: Double, z: Double) : this(service, worldName, x, y, z, 0F, 0F)

    constructor(serviceName: String, worldName: String, x: Double, y: Double, z: Double) : this(serviceName, worldName, x, y, z, 0F, 0F)

    /**
     * Returns this location converted to a [GroupLocation]
     */
    fun toGroupLocation() = GroupLocation(groupName, worldName, x, y, z, yaw, pitch)

    /**
     * Returns the service this location is belongs to.
     */
    fun getService() = CloudAPI.instance.getCloudServiceManger().getCloudServiceByName(serviceName)

    override fun add(x: Double, y: Double, z: Double): ServiceLocation {
        return ServiceLocation(this.serviceName, this.worldName, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch)
    }

    override fun setWorldName(worldName: String): ServiceLocation {
        return ServiceLocation(this.serviceName, worldName, this.x, this.y, this.z, this.yaw, this.pitch)
    }

    /**
     * Returns a new [ServiceLocation] with a changed [serviceName]
     */
    fun setServiceName(serviceName: String): ServiceLocation {
        return ServiceLocation(serviceName, this.worldName, this.x, this.y, this.z, this.yaw, this.pitch)
    }

}