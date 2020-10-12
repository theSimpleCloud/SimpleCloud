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

package eu.thesimplecloud.module.sign.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.module.serviceselection.api.AbstractServiceViewer
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.lib.layout.LayoutType
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.module.sign.service.event.BukkitCloudSignUpdatedEvent
import eu.thesimplecloud.plugin.extension.toBukkitLocation
import org.bukkit.block.Sign

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 18:25
 * @author Frederick Baier
 */
class BukkitCloudSign(
        val cloudSign: CloudSign
) : AbstractServiceViewer() {

    val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(cloudSign.forGroup)
    val location = cloudSign.templateLocation.toBukkitLocation()
    val templateLocation = cloudSign.templateLocation


    override fun updateView() {
        if (serviceGroup == null) {
            println("[SimpleCloud-Sign] WARNING: Cannot find group by name: ${cloudSign.forGroup}")
            return
        }
        if (location == null) {
            println("[SimpleCloud-Sign] WARNING: Cannot find world by name: ${cloudSign.templateLocation.worldName}")
            return
        }
        if (location.block.state !is Sign) {
            return
        }
        val currentServer = this.service
        val sign = location.block.state as Sign
        clearSign(false)

        val config = SignModuleConfig.getConfig()
        val layoutType = calculateLayoutType()
        val signLayout = config.getSignLayoutForGroup(layoutType, this.serviceGroup.getName())

        val currentFrame = signLayout.getCurrentFrame()
        for (i in 0 until 4) {
            sign.setLine(i, replacePlaceholders(currentFrame.lines[i], currentServer))
        }
        sign.update()

        CloudAPI.instance.getEventManager().call(BukkitCloudSignUpdatedEvent(this))
    }

    private fun replacePlaceholders(lineToReplace: String, currentServer: ICloudService?): String {
        var lineToReplace = lineToReplace
        if (currentServer != null)
            PLACEHOLDERS.forEach { lineToReplace = it.replacePlaceholder(currentServer, lineToReplace) }
        GROUP_PLACEHOLDERS.forEach { lineToReplace = it.replacePlaceholder(this.serviceGroup!!, lineToReplace) }
        return lineToReplace
    }

    private fun calculateLayoutType(): LayoutType {
        if (this.serviceGroup!!.isInMaintenance()) return LayoutType.MAINTENANCE
        if (this.service == null || service!!.getState() == ServiceState.CLOSED) return LayoutType.SEARCHING
        if (this.service!!.getState() == ServiceState.STARTING) return LayoutType.STARTING
        return LayoutType.ONLINE
    }

    override fun removeView() {
        this.clearSign(true)
    }

    private fun clearSign(update: Boolean = true) {
        val location = this.location!!
        if (location.block.state is Sign) {
            val sign = location.block.state as Sign
            for (i in 0 until 4) {
                sign.setLine(i, "")
            }
            if (update) sign.update()
        }
    }


    companion object {
        private val PLACEHOLDERS = listOf<Placeholder<ICloudService>>(
                Placeholder("SERVICE") { it.getName() },
                Placeholder("ONLINE_PLAYERS") { it.getOnlineCount().toString() },
                Placeholder("ONLINE_COUNT") { it.getOnlineCount().toString() },
                Placeholder("MOTD") { it.getMOTD() },
                Placeholder("HOST") { it.getHost() },
                Placeholder("PORT") { it.getPort().toString() },
                Placeholder("STATE") { it.getState().name },
                Placeholder("NUMBER") { it.getServiceNumber().toString() },
                Placeholder("WRAPPER") { it.getWrapperName()!! }
        )

        private val GROUP_PLACEHOLDERS = listOf<Placeholder<ICloudServiceGroup>>(
                Placeholder("GROUP") { it.getName() },
                Placeholder("MAX_PLAYERS") { it.getMaxPlayers().toString() },
                Placeholder("TEMPLATE") { it.getTemplateName() }
        )
    }
}