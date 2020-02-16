package eu.thesimplecloud.plugin.extension

import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.IllegalStateException

/**
 * Returns the bukkit location with the content of this location.
 * Returns null if the world was not found.
 */
fun SimpleLocation.toBukkitLocation(): Location? {
    val world = Bukkit.getWorld(this.worldName) ?: return null
    return Location(world, x, y, z, yaw, pitch)
}

/**
 * Returns a this location parsed to a [ServiceLocation]
 */
fun Location.toCloudLocation(): ServiceLocation {
    if (this.world == null) throw IllegalStateException("World must be not null.")
    return ServiceLocation(CloudPlugin.instance.thisService(), this.world!!.name, this.x, this.y, this.z, this.yaw, this.pitch)
}