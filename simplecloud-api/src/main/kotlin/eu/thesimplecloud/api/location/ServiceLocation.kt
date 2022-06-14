/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

    constructor(
        service: ICloudService,
        worldName: String,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float
    ) : this(service.getName(), worldName, x, y, z, yaw, pitch)

    constructor(service: ICloudService, worldName: String, x: Double, y: Double, z: Double) : this(
        service,
        worldName,
        x,
        y,
        z,
        0F,
        0F
    )

    constructor(serviceName: String, worldName: String, x: Double, y: Double, z: Double) : this(
        serviceName,
        worldName,
        x,
        y,
        z,
        0F,
        0F
    )

    /**
     * Returns this location converted to a [GroupLocation]
     */
    fun toGroupLocation() = GroupLocation(groupName, worldName, x, y, z, yaw, pitch)

    /**
     * Returns the template location of this location
     */
    fun toTemplateLocation(): TemplateLocation {
        val service = getService() ?: throw IllegalStateException("Service $serviceName cannot be found")
        return TemplateLocation(service.getTemplateName(), worldName, x, y, z, yaw, pitch)
    }

    /**
     * Returns the service this location is belongs to.
     */
    fun getService() = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)

    override fun add(x: Double, y: Double, z: Double): ServiceLocation {
        return ServiceLocation(
            this.serviceName,
            this.worldName,
            this.x + x,
            this.y + y,
            this.z + z,
            this.yaw,
            this.pitch
        )
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ServiceLocation) return false
        if (!super.equals(other)) return false

        if (serviceName != other.serviceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + serviceName.hashCode()
        return result
    }

}