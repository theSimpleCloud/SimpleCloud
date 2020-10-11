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

package eu.thesimplecloud.module.sign.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.group.CloudServiceGroupUpdatedEvent
import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.module.serviceselection.api.ServiceViewGroupManager
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.module.sign.lib.sign.CloudSignContainer
import eu.thesimplecloud.module.sign.service.command.CloudSignsCommand
import eu.thesimplecloud.module.sign.service.listener.InteractListener
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.event.EventHandler
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 18:00
 * @author Frederick Baier
 */
class BukkitPluginMain : JavaPlugin() {

    private lateinit var serviceViewManager: SignServiceViewManager

    override fun onEnable() {
        SignAPI(this)
        serviceViewManager = SignAPI.instance.serviceViewManager
        val config = SignModuleConfig.getConfig()
        CloudAPI.instance.getCloudServiceGroupManager().getServerOrLobbyGroups().forEach {
            setupGroup(it, config)
        }

        server.pluginManager.registerEvents(InteractListener(), this)
        getCommand("cloudsigns")!!.setExecutor(CloudSignsCommand())

        CloudAPI.instance.getEventManager().registerListener(CloudAPI.instance.getThisSidesCloudModule(), object : IListener {

            @CloudEventHandler
            fun handleUpdate(event: GlobalPropertyUpdatedEvent) {
                if (event.propertyName == "sign-config") {
                    val property = event.property as IProperty<SignModuleConfig>
                    updateSigns(property.getValue().signContainer)
                }
            }

            @EventHandler
            fun handleServiceGroupUpdate(event: CloudServiceGroupUpdatedEvent) {
                if (event.serviceGroup is ICloudServerGroup)
                    setupGroup(event.serviceGroup, SignModuleConfig.getConfig())
            }

        })

    }

    private fun updateSigns(signContainer: CloudSignContainer) {

        val signsForTemplate = signContainer.getSignsForTemplate(CloudPlugin.instance.thisService().getTemplate())

        //remove removed BukkitCloudSigns
        val serverGroups = CloudAPI.instance.getCloudServiceGroupManager().getServerOrLobbyGroups()
        for (group in serverGroups) {
            val groupView = serviceViewManager.getGroupView(group)
            val signsForThisGroup = signsForTemplate.filter { it.forGroup == group.getName() }
            val serviceViewers = groupView.getServiceViewers()
            val bukkitSignsToUnregister = serviceViewers.filter { !signsForThisGroup.contains(it.cloudSign) }
            bukkitSignsToUnregister.forEach { groupView.removeServiceViewer(it) }
        }

        //add added BukkitCloudSigns
        val registeredCloudSigns = serverGroups.map { serviceViewManager.getGroupView(it) }
                .map { it.getServiceViewers() }.flatten()

        signsForTemplate.forEach { cloudSign ->
            if (!isSignRegistered(cloudSign, registeredCloudSigns)) {
                registerCloudSign(cloudSign)
            }
        }
    }

    private fun registerCloudSign(cloudSign: CloudSign) {
        val groupView = this.serviceViewManager.getGroupView(cloudSign.getGroup())
        groupView.addServiceViewers(BukkitCloudSign(cloudSign))
    }

    private fun isSignRegistered(cloudSign: CloudSign, allRegisteredSigns: List<BukkitCloudSign>): Boolean {
        return allRegisteredSigns.map { it.cloudSign }.contains(cloudSign)
    }

    private fun setupGroup(group: ICloudServiceGroup, config: SignModuleConfig) {
        if (this.serviceViewManager.isGroupViewRegistered(group)) return
        val serviceViewGroupManager = ServiceViewGroupManager<BukkitCloudSign>(group)
        val signsForTemplate = config.signContainer.getSignsForTemplate(CloudPlugin.instance.thisService().getTemplate())
        val signsToRegister = signsForTemplate.filter { it.forGroup == group.getName() }
        val bukkitSigns = signsToRegister.map { BukkitCloudSign(it) }
        serviceViewGroupManager.addServiceViewers(*bukkitSigns.toTypedArray())
        this.serviceViewManager.addServiceViewGroupManager(serviceViewGroupManager)
    }

}