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

package eu.thesimplecloud.plugin.extension

import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import org.bukkit.Location

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