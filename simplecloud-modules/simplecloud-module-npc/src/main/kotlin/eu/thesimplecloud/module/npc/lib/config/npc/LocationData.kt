package eu.thesimplecloud.module.npc.lib.config.npc

data class LocationData(
    var locationGroup: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0F,
    val pitch: Float = 90F
)