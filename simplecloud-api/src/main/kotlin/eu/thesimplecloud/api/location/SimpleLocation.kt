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

open class SimpleLocation(
        val worldName: String,
        val x: Double,
        val y: Double,
        val z: Double,
        val yaw: Float,
        val pitch: Float
) {
    constructor(worldName: String, x: Double, y: Double, z: Double) : this(worldName, x, y, z, 0F, 0F)

    /**
     * Returns a new location with the specified [x], [y], [z] values added to the current ones.
     */
    open fun add(x: Double, y: Double, z: Double): SimpleLocation {
        return SimpleLocation(this.worldName, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch)
    }

    /**
     * Returns a new location with a different [worldName]
     */
    open fun setWorldName(worldName: String): SimpleLocation {
        return SimpleLocation(worldName, this.x, this.y, this.z, this.yaw, this.pitch)
    }

    /**
     * Returns a service location with the specified [serviceName]
     */
    fun toServiceLocation(serviceName: String): ServiceLocation {
        return ServiceLocation(serviceName, worldName, x, y, z, yaw, pitch)
    }



    override fun hashCode(): Int {
        var result = worldName.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleLocation) return false

        if (worldName != other.worldName) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (yaw != other.yaw) return false
        if (pitch != other.pitch) return false

        return true
    }
}