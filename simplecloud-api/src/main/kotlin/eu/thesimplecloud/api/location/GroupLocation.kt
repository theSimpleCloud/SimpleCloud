package eu.thesimplecloud.api.location

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

open class GroupLocation(
        val serviceGroupName: String,
        worldName: String,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float
) : SimpleLocation(worldName, x, y, z, yaw, pitch) {

    /**
     * Returns the group this location belongs to.
     */
    fun getGroup(): ICloudServiceGroup? = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(serviceGroupName)

    override fun add(x: Double, y: Double, z: Double): GroupLocation {
        return GroupLocation(this.serviceGroupName, this.worldName, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch)
    }

    override fun setWorldName(worldName: String): GroupLocation {
        return GroupLocation(this.serviceGroupName, worldName, this.x, this.y, this.z, this.yaw, this.pitch)
    }

}