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

package eu.thesimplecloud.module.sign.service.nukkit.listener

import cn.nukkit.blockentity.BlockEntitySign
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerInteractEvent
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.module.sign.service.nukkit.NukkitSignAPI
import eu.thesimplecloud.plugin.extension.getCloudPlayer

class InteractListener : Listener {

    @EventHandler
    fun on(event: PlayerInteractEvent) {
        if (event.isCancelled) return
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
            return
        val clickedBlock = event.block ?: return
        val blockEntity = clickedBlock.level.getBlockEntity(clickedBlock.location)
        if (blockEntity is BlockEntitySign) {
            val nukkitCloudSign = NukkitSignAPI.instance.serviceViewManager.getNukkitCloudSignByLocation(blockEntity.location)
                    ?: return
            if (nukkitCloudSign.serviceGroup?.isInMaintenance() == true) return
            val currentServer = nukkitCloudSign.service ?: return
            if (currentServer.getState() != ServiceState.VISIBLE) return
            event.player.getCloudPlayer().connect(currentServer)
        }
    }

}