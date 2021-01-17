package eu.thesimplecloud.module.prefix.service.listener

import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.module.permission.event.player.PermissionPlayerUpdatedEvent
import eu.thesimplecloud.module.prefix.service.tablist.TablistHelper
import org.bukkit.Bukkit

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 19.12.2020
 * Time: 16:24
 */
class CloudListener : IListener {

    @CloudEventHandler
    fun handlePermissionUpdate(event: PermissionPlayerUpdatedEvent) {
        val player = Bukkit.getPlayer(event.player.getUniqueId())?: return
        TablistHelper.updateScoreboardForAllPlayers()
    }

    @CloudEventHandler
    fun handlePropertyChange(event: GlobalPropertyUpdatedEvent) {
        if (event.propertyName == "prefix-config") {
            TablistHelper.load()
        }
    }

}