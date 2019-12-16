package eu.thesimplecloud.lib.location

open class GroupLocation(
        val serviceGroupName: String,
        worldName: String,
        x: Double,
        y: Double,
        z: Double
) : SimpleLocation(worldName, x, y, z)