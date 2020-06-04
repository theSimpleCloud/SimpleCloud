/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

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