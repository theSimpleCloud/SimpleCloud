package eu.thesimplecloud.module.prefix.service.command

import eu.thesimplecloud.module.prefix.config.Config
import eu.thesimplecloud.module.prefix.service.spigot.BukkitPluginMain
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ChatTabCommand(val plugin: BukkitPluginMain) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            return true
        }

        val player = sender

        if(!player.hasPermission("cloud.module.chat-tab.manage")) {
            player.sendConfigMessage(getMessage("noPermissions"))
            return true
        }

        if(args.size != 1 && args.size != 2) {
            player.sendConfigMessage(getMessage("commandSyntax"))
            return true
        }

        if(args.size == 1) {
            if(args[0].equals("reload", true)) {
                plugin.delayConfiguration.load()
                player.sendConfigMessage(getMessage("reloadSuccess"))
                player.sendCurrentDelay()
                return true
            }

            if(args[0].equals("save", true)) {
                plugin.delayConfiguration.save()
                player.sendConfigMessage(getMessage("saveSuccess"))
                return true
            }

            if(args[0].equals("info", true)) {
                plugin.delayConfiguration.save()
                player.sendCurrentDelay()
                return true
            }

            player.sendConfigMessage(getMessage("commandSyntax"))

            return true
        }

        if(args[0].equals("set", true)) {

            val delay = args[1].toLongOrNull() ?: 20L
            plugin.delayConfiguration.delay = delay
            player.sendConfigMessage(getMessage("modifySuccess"), listOf(Pair("%DELAY%", delay.toString())))

            return true
        }

        player.sendConfigMessage(getMessage("commandSyntax"))
        return true
    }

    private fun getMessage(key: String): String {
        return Config.getConfig().messages[key] ?: key
    }

    private fun Player.sendConfigMessage(message: String, replacement: List<Pair<String, String>> = emptyList()) {

        var tempMessage = message

        replacement.forEach {
            tempMessage = tempMessage.replace(it.first, it.second)
        }

        sendMessage(Config.getConfig().messages["prefix"]?.let {

            tempMessage.replace("%PREFIX%", it)

        } ?: tempMessage)
    }

    private fun Player.sendCurrentDelay() {
        sendConfigMessage(getMessage("currentDelay"), listOf(Pair("%DELAY%", plugin.delayConfiguration.delay.toString()), Pair("%SECONDS%", (plugin.delayConfiguration.delay.toDouble() / 20).toString())))
    }

}

