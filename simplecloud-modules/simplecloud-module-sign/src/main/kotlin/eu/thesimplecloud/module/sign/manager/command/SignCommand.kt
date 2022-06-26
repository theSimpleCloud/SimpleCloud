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

package eu.thesimplecloud.module.sign.manager.command

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.module.sign.lib.group.GroupLayouts
import eu.thesimplecloud.module.sign.lib.layout.LayoutType
import eu.thesimplecloud.module.sign.manager.SignModuleConfigPersistence

/**
 * Created by IntelliJ IDEA.
 * Date: 11.10.2020
 * Time: 19:16
 * @author Frederick Baier
 */
@Command("sign", CommandType.CONSOLE_AND_INGAME, "cloud.module.sign")
class SignCommand : ICommandHandler {

    @CommandSubPath("reload", "Reloads the sign module")
    fun handleReload(sender: ICommandSender) {
        val config = SignModuleConfigPersistence.load()
        config.update()
        sender.sendProperty("manager.command.sign.reload")
    }

    @CommandSubPath("layout <group> <layoutType> <layout>", "Sets the layout for a group")
    fun handleEditLayout(
        sender: ICommandSender,
        @CommandArgument("group") groupName: String,
        @CommandArgument("layoutType") layoutType: LayoutType,
        @CommandArgument("layout") layoutName: String
    ) {
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (group == null) {
            sender.sendProperty("manager.command.sign.layout.group-not-found")
            return
        }
        val config = SignModuleConfigPersistence.load()
        val signLayout = config.signLayoutContainer.getLayoutByName(layoutType, layoutName)
        if (signLayout == null) {
            sender.sendProperty("manager.command.sign.layout.layout-not-found")
            return
        }
        val groupsLayoutContainer = config.groupsLayoutContainer
        if (groupsLayoutContainer.hasGroupLayout(group)) {
            val groupLayouts = groupsLayoutContainer.getGroupLayoutsByGroupName(group.getName())!!
            groupLayouts.setLayout(layoutType, layoutName)
        } else {
            val groupsLayout = GroupLayouts(group.getName(), mutableMapOf(layoutType to layoutName))
            groupsLayoutContainer.addGroupLayouts(groupsLayout)
        }
        config.update()
        SignModuleConfigPersistence.save(config)
        sender.sendProperty("manager.command.sign.layout.success", group.getName(), layoutName, layoutType.toString())
    }

}