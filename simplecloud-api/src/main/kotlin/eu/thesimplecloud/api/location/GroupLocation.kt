package eu.thesimplecloud.api.location

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

open class GroupLocation(
        private val serviceGroupName: String,
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

    /**
     * Returns the template location of this location
     */
    fun toTemplateLocation(): TemplateLocation {
        val group = getGroup() ?: throw IllegalStateException("Group $serviceGroupName cannot be found")
        return TemplateLocation(group.getTemplateName(), worldName, x, y, z, yaw, pitch)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupLocation) return false
        if (!super.equals(other)) return false

        if (serviceGroupName != other.serviceGroupName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + serviceGroupName.hashCode()
        return result
    }

}