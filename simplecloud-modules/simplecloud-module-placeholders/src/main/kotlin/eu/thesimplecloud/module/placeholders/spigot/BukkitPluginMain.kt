package eu.thesimplecloud.module.placeholders.spigot

import eu.thesimplecloud.module.placeholders.spigot.placeholderapi.PlaceholderHook
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BukkitPluginMain : JavaPlugin() {

    val replacePermissionModule = Bukkit.getPluginManager().getPlugin("SimpleCloud-Permission") != null
    val replaceChatTabModule = Bukkit.getPluginManager().getPlugin("SimpleCloud-Chat-Tab") != null

    override fun onEnable() {

        if(!replacePermissionModule) {
            println("Could not replace placeholders for permission module. Please make sure that the permission module is loaded correctly if you want to use placeholders for that.")
        }

        if(!replaceChatTabModule) {
            println("Could not replace placeholders for chat-tab module. Please make sure that the chat-tab module is loaded correctly if you want to use placeholders for that.")
        }

        val register = PlaceholderHook(this).register()

        if(register) println("[INFO] Registered PlaceholderAPI hook successfully!")
        else println("[FATAL] Failed to register PlaceholderAPI hook!")
    }

}