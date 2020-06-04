/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.extension.sendMessage

@Command("execute", CommandType.CONSOLE_AND_INGAME, "cloud.command.execute", ["exec"])
class ExecuteCommand : ICommandHandler {

    @CommandSubPath("", "Execute a command on the specified service")
    fun handle(commandSender: ICommandSender, args: Array<String>) {
        if (args.size < 2) {
            val prefix = if (commandSender is ICloudPlayer) "cloud " else ""
            commandSender.sendMessage("manager.command.execute.usage", "&cUsage: ${prefix}execute <service> <command>")
            return
        }
        val serviceName = args[0]
        val service = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(serviceName)
        if (service == null) {
            commandSender.sendMessage("manager.command.execute.service-not-found", "&cService not found.")
            return
        }
        service.executeCommand(args.drop(1).joinToString(" "))
        commandSender.sendMessage("manager.command.execute.success", "&aExecuted command.")
    }

}