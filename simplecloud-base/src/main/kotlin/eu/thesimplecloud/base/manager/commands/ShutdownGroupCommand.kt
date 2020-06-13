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

import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.console.command.provider.ServiceGroupCommandSuggestionProvider
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("shutdowngroup", CommandType.CONSOLE_AND_INGAME, "cloud.command.shutdowngroup")
class ShutdownGroupCommand : ICommandHandler {

    @CommandSubPath("<group>", "Stops all services of a group.")
    fun startService(commandSender: ICommandSender, @CommandArgument("group", ServiceGroupCommandSuggestionProvider::class) cloudServiceGroup: ICloudServiceGroup) {
        if (cloudServiceGroup.getAllServices().isEmpty()){
            commandSender.sendMessage("manager.command.shutdowngroup.failure", "There are no running services of group %GROUP%", cloudServiceGroup.getName())
            return
        }
        cloudServiceGroup.shutdownAllServices()
        commandSender.sendMessage("manager.command.shutdowngroup.success", "Stopping all services of group %GROUP%", cloudServiceGroup.getName())
    }

}