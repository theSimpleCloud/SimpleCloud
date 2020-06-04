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

package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.network.packets.screen.PacketIOExecuteCommand
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.screen.ICommandExecuteManager
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher

class CommandExecuteManagerImpl : ICommandExecuteManager {

    override fun executeCommand(commandExecutable: ICommandExecutable, command: String) {
        if (commandExecutable is ICloudService && commandExecutable.getWrapper() == Wrapper.instance.getThisWrapper()) {
            val serviceProcess = Wrapper.instance.cloudServiceProcessManager.getCloudServiceProcessByServiceName(commandExecutable.getName())
            serviceProcess?.executeCommand(command)
            return
        }
        if (commandExecutable is IWrapperInfo && commandExecutable == Wrapper.instance.getThisWrapper()) {
            Launcher.instance.executeCommand(command)
            return
        }
        val cloudClientType = if (commandExecutable is ICloudService) NetworkComponentType.SERVICE else NetworkComponentType.WRAPPER
        Wrapper.instance.communicationClient.sendUnitQuery(PacketIOExecuteCommand(cloudClientType, commandExecutable.getName(), command))
    }
}