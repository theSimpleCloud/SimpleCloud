package eu.thesimplecloud.module.permission.service.spigot

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.service.spigot.util.ReflectionUtils
import eu.thesimplecloud.plugin.extension.getCloudPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.lang.reflect.Field

class SpigotListener : Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PlayerLoginEvent) {
        try {
            val clazz: Class<*>? = ReflectionUtils.reflectCraftClazz(".entity.CraftHumanEntity")
            var field: Field? = null
            println("class: $clazz")
            if (clazz != null) {
                field = clazz.getDeclaredField("perm")
                println("field: $field")
            }
            if (field == null) {
                println("WARNING: Permission field was null")
                return
            }
            field.isAccessible = true
            field[event.player] = BukkitCloudPermissibleBase(event.player)
        } catch (ex: NoSuchFieldException) {
            ex.printStackTrace()
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        } catch (ex: IllegalAccessException) {
            ex.printStackTrace()
        }
        println(event.player.getCloudPlayer().getProperties())
        println(JsonData.fromObject(PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(event.player.uniqueId)!!).getAsJsonString())
    }

}