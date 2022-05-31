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

package eu.thesimplecloud.base.manager.commands

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.service.start.configuration.ServiceStartConfiguration
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider

@Command("startStatic", CommandType.CONSOLE_AND_INGAME, "cloud.command.startstatic")
class StartStaticCommand : ICommandHandler {

    @CommandSubPath("<service>", "Starts a static service")
    fun handleStartStatic(
        commandSender: ICommandSender,
        @CommandArgument("service", ServiceGroupCommandSuggestionProvider::class) serviceName: String
    ) {
        val runningService = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        if (runningService != null) {
            commandSender.sendProperty("manager.command.startstatic.service-already-online")
            return
        }
        val groupName = serviceName.split("-").dropLast(1).joinToString("-")
        val numberString = serviceName.split("-").last()
        val number = kotlin.runCatching { numberString.toInt() }.getOrNull()
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(groupName)
        if (number == null || serviceGroup == null) {
            commandSender.sendProperty("manager.command.startstatic.service-invalid")
            return
        }

        if (!serviceGroup.isStatic()) {
            commandSender.sendProperty("manager.command.startstatic.group-not-static")
            return
        }

        Manager.instance.serviceHandler.startService(ServiceStartConfiguration(serviceGroup).setServiceNumber(number))
        commandSender.sendProperty("manager.command.startstatic.success")
    }

}

