package eu.thesimplecloud.module.placeholders.spigot

import eu.thesimplecloud.module.placeholders.spigot.placeholderapi.PlaceholderHook
import org.bukkit.plugin.java.JavaPlugin

class BukkitPluginMain : JavaPlugin() {

    var replacePermissionModule = false
    var replaceChatTabModule = false

    override fun onEnable() {

        checkPermissionsModule()
        checkChatTabModule()

        val register = PlaceholderHook(this).register()

        if(register) println("[INFO] Registered PlaceholderAPI hook successfully!")
        else println("[FATAL] Failed to register PlaceholderAPI hook!")
    }

    fun checkPermissionsModule() {
        try {
            val forName = Class.forName("eu.thesimplecloud.module.permission.manager.PermissionModule")
            println("${forName.name} exists, will replace placeholders for permission module.")
            replacePermissionModule = true

        } catch (exception: Exception) {
            println("Could not replace placeholders for permission module. Please make sure that the permission module is loaded correctly if you want to use placeholders for that.")
            exception.printStackTrace()
        }
    }

    fun checkChatTabModule() {
        try {
            val forName = Class.forName("eu.thesimplecloud.module.prefix.manager.PrefixModule")
            println("${forName.name} exists, will replace placeholders for chat-tab module.")
            replaceChatTabModule = true

        } catch (exception: Exception) {
            println("Could not replace placeholders for chat-tab module. Please make sure that the chat-tab module is loaded correctly if you want to use placeholders for that.")
            exception.printStackTrace()
        }
    }

}