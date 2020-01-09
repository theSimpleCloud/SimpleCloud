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
}