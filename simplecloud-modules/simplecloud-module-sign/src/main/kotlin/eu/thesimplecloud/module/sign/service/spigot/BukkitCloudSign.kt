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

package eu.thesimplecloud.module.sign.service.spigot

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.module.sign.service.lib.AbstractCloudSign
import eu.thesimplecloud.module.sign.service.spigot.event.BukkitCloudSignUpdatedEvent
import eu.thesimplecloud.plugin.extension.toBukkitLocation
import org.bukkit.block.Sign

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 18:25
 * @author Frederick Baier
 */
class BukkitCloudSign(
    cloudSign: CloudSign
) : AbstractCloudSign(cloudSign) {

    val bukkitLocation = cloudSign.templateLocation.toBukkitLocation()

    override fun shallUpdateSign(): Boolean {
        if (bukkitLocation == null) {
            println("[SimpleCloud-Sign] WARNING: Cannot find world by name: ${cloudSign.templateLocation.worldName}")
            return false
        }
        if (!bukkitLocation.chunk.isLoaded) {
            return false
        }
        if (bukkitLocation.block.state !is Sign) {
            return false
        }
        return true
    }

    override fun updateSignLines(lines: List<String>) {
        val sign = this.bukkitLocation!!.block.state as Sign
        lines.forEachIndexed { index, line ->
            sign.setLine(index, line)
        }
        sign.update()

        CloudAPI.instance.getEventManager().call(BukkitCloudSignUpdatedEvent(this))
    }

    override fun clearSign(update: Boolean) {
        val location = this.bukkitLocation!!
        if (location.block.state is Sign) {
            val sign = location.block.state as Sign
            for (i in 0 until 4) {
                sign.setLine(i, "")
            }
            if (update) sign.update()
        }
    }
}