package eu.thesimplecloud.module.sign.service.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.module.sign.lib.CloudSign
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.plugin.extension.toCloudLocation
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class CloudSignsCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val block: Block = sender.getTargetBlock(null, 4)
        if (block.state !is Sign) {
            sender.sendMessage("§cYou must look at a sign.")
            return true
        }
        if (args.isEmpty()) {
            sender.sendMessage("§cUsage: /cloudsigns <add/remove> [group]")
            return true
        }
        when(args[0].toLowerCase()) {
            "add" -> {
                if (args.size != 2) {
                    sender.sendMessage("§cUsage: /cloudsigns <add> <group>")
                    return true
                }
                val groupName = args[1]
                val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
                if (serviceGroup == null) {
                    sender.sendMessage("§cGroup not found.")
                    return true
                }
                if (serviceGroup.getServiceType() == ServiceType.PROXY) {
                    sender.sendMessage("§cCannot create a sign for proxy groups.")
                    return true
                }
                val signModuleConfig = SignModuleConfig.INSTANCE
                val templateLocation = block.location.toCloudLocation().toTemplateLocation()
                val cloudSignByLocation = signModuleConfig.getCloudSignByLocation(templateLocation)
                if (cloudSignByLocation != null) {
                    sender.sendMessage("§cThere is already a sign registered on this location.")
                    return true
                }
                signModuleConfig.cloudSigns.add(CloudSign(templateLocation, serviceGroup.getName()))

                if (signModuleConfig.groupToLayout.getLayoutByGroupName(groupName) == null) {
                    signModuleConfig.groupToLayout.putGroupToLayout(groupName, "default")
                }

                signModuleConfig.update()
                sender.sendMessage("§aSign added.")
            }
            "remove" -> {
                val signModuleConfig = SignModuleConfig.INSTANCE
                val cloudSign = signModuleConfig.getCloudSignByLocation(block.location.toCloudLocation().toTemplateLocation())
                if (cloudSign == null) {
                    sender.sendMessage("§cSign is not registered.")
                    return true
                }
                signModuleConfig.cloudSigns.remove(cloudSign)
                signModuleConfig.update()
                sender.sendMessage("§aSign removed.")
            }
            else -> {
                sender.sendMessage("§cUsage: /cloudsigns <add/remove> [group]")
                return true
            }
        }
        return true
    }
}