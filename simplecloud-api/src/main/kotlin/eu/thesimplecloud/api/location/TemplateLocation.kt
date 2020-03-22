package eu.thesimplecloud.api.location

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.ITemplate

class TemplateLocation(
        private val templateName: String,
        worldName: String,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float
) : SimpleLocation(worldName, x, y, z, yaw, pitch) {

    /**
     * Returns the template this location belongs to.
     */
    fun getTemplate(): ITemplate? = CloudAPI.instance.getTemplateManager().getTemplateByName(templateName)

    override fun add(x: Double, y: Double, z: Double): TemplateLocation {
        return TemplateLocation(this.templateName, this.worldName, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch)
    }

    override fun setWorldName(worldName: String): TemplateLocation {
        return TemplateLocation(this.templateName, this.worldName, this.x, this.y, this.z, this.yaw, this.pitch)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TemplateLocation) return false
        if (!super.equals(other)) return false

        if (templateName != other.templateName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + templateName.hashCode()
        return result
    }

}