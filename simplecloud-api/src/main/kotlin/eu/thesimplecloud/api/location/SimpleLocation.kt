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

    open fun add(x: Double, y: Double, z: Double): SimpleLocation {
        return SimpleLocation(this.worldName, this.x + x, this.y + y, this.z + z, this.yaw, this.pitch)
    }
}