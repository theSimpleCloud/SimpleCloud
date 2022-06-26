/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.module.sign.service.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.plugin.extension.toCloudLocation
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * Date: 11.10.2020
 * Time: 12:05
 * @author Frederick Baier
 */
class CloudSignsCommand : CommandExecutor {

    private val thisService = CloudPlugin.instance.thisService()

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
        when (args[0].toLowerCase()) {
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
                val signModuleConfig = SignModuleConfig.getConfig()
                val templateLocation = block.location.toCloudLocation().toTemplateLocation()
                val cloudSignByLocation = signModuleConfig.signContainer.getCloudSignByLocation(
                    this.thisService.getTemplate(),
                    templateLocation
                )
                if (cloudSignByLocation != null) {
                    sender.sendMessage("§cThere is already a sign registered on this location.")
                    return true
                }
                signModuleConfig.signContainer.addSign(CloudSign(templateLocation, serviceGroup.getName()))

                signModuleConfig.update()
                sender.sendMessage("§aSign added.")
            }
            "remove" -> {
                val signModuleConfig = SignModuleConfig.getConfig()
                val templateLocation = block.location.toCloudLocation().toTemplateLocation()
                val cloudSign = signModuleConfig.signContainer.getCloudSignByLocation(
                    this.thisService.getTemplate(),
                    templateLocation
                )
                if (cloudSign == null) {
                    sender.sendMessage("§cSign is not registered.")
                    return true
                }
                signModuleConfig.signContainer.removeSign(cloudSign)
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