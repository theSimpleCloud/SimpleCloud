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