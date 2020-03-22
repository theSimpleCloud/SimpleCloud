package eu.thesimplecloud.module.sign.service.listener

import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.service.SpigotPluginMain
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import eu.thesimplecloud.plugin.extension.toCloudLocation
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class InteractListener : Listener {

    @EventHandler
    fun on(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK)
            return
        val clickedBlock = event.clickedBlock ?: return
        val state = clickedBlock.state
        if (state is Sign) {
            val bukkitCloudSign = SpigotPluginMain.INSTANCE.bukkitCloudSignManager.getBukkitCloudSignByLocation(state.location)
            bukkitCloudSign ?: return
            if (bukkitCloudSign.serviceGroup?.isInMaintenance() == true) return
            val currentServer = bukkitCloudSign.currentServer ?: return
            if (currentServer.getState() != ServiceState.VISIBLE) return
            event.player.getCloudPlayer().connect(currentServer)
        }
    }

}