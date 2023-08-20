package eu.thesimplecloud.module.placeholders.spigot.placeholderapi

import eu.thesimplecloud.module.placeholders.spigot.BukkitPluginMain
import eu.thesimplecloud.module.placeholders.spigot.replace
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PlaceholderHook(private val plugin: BukkitPluginMain) : PlaceholderExpansion() {

    override fun getIdentifier(): String = "CLOUD"

    override fun getAuthor(): String = "rlqu, Panda"

    override fun getVersion(): String = "2.5.0"

    override fun canRegister(): Boolean = true

    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer, params: String): String {
        return ("%$params%").replace(player.uniqueId, plugin.replacePermissionModule, plugin.replaceChatTabModule)
    }

}