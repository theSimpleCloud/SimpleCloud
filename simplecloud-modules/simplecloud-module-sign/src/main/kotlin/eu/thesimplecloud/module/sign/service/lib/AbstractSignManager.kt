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

package eu.thesimplecloud.module.sign.service.lib

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.group.CloudServiceGroupUpdatedEvent
import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.module.serviceselection.api.AbstractServiceViewManager
import eu.thesimplecloud.module.serviceselection.api.ServiceViewGroupManager
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.lib.sign.CloudSign
import eu.thesimplecloud.module.sign.lib.sign.CloudSignContainer
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.event.EventHandler

/**
 * Created by IntelliJ IDEA.
 * Date: 04/02/2021
 * Time: 14:19
 * @author Frederick Baier
 */
abstract class AbstractSignManager(
    abstractServiceViewManager: AbstractServiceViewManager<out AbstractCloudSign>
) {

    protected val abstractServiceViewManager: AbstractServiceViewManager<AbstractCloudSign> = abstractServiceViewManager as AbstractServiceViewManager<AbstractCloudSign>

    init {
        initListener()
    }

    fun setupRegisteredGroups() {
        val config = SignModuleConfig.getConfig()
        CloudAPI.instance.getCloudServiceGroupManager().getServerOrLobbyGroups().forEach {
            setupGroup(it, config)
        }
    }

    private fun initListener() {
        CloudAPI.instance.getEventManager().registerListener(CloudAPI.instance.getThisSidesCloudModule(), object :
            IListener {

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
            val groupView = abstractServiceViewManager.getGroupView(group)
            val signsForThisGroup = signsForTemplate.filter { it.forGroup == group.getName() }
            val serviceViewers = groupView.getServiceViewers()
            val bukkitSignsToUnregister = serviceViewers.filter { !signsForThisGroup.contains(it.cloudSign) }
            bukkitSignsToUnregister.forEach { unregisterSign(it, groupView) }
        }

        //add added BukkitCloudSigns
        val registeredCloudSigns = serverGroups.map { abstractServiceViewManager.getGroupView(it) }
            .map { it.getServiceViewers() }.flatten()

        signsForTemplate.forEach { cloudSign ->
            if (!isSignRegistered(cloudSign, registeredCloudSigns)) {
                registerCloudSign(cloudSign)
            }
        }
    }

    private fun setupGroup(group: ICloudServiceGroup, config: SignModuleConfig) {
        if (this.abstractServiceViewManager.isGroupViewRegistered(group)) return
        this.abstractServiceViewManager.createServiceViewGroupManager(group)
        val signsForTemplate = config.signContainer.getSignsForTemplate(CloudPlugin.instance.thisService().getTemplate())
        val signsToRegister = signsForTemplate.filter { it.forGroup == group.getName() }
        signsToRegister.forEach {  registerCloudSign(it) }

    }

    protected fun isSignRegistered(cloudSign: CloudSign, allRegisteredSigns: List<AbstractCloudSign>): Boolean {
        return allRegisteredSigns.map { it.cloudSign }.contains(cloudSign)
    }

    abstract fun unregisterSign(abstractCloudSign: AbstractCloudSign, groupView: ServiceViewGroupManager<AbstractCloudSign>)

    abstract fun registerCloudSign(cloudSign: CloudSign)


}