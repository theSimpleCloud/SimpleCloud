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

package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.launcher.event.module.ModuleLoadedEvent
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.startup.Launcher

class ModuleEventListener : IListener {

    @CloudEventHandler
    fun handleLoad(event: ModuleLoadedEvent) {
        val module = event.module
        Launcher.instance.consoleSender.sendProperty("manager.module.loaded", module.fileContent.name, module.fileContent.author)
    }

    @CloudEventHandler
    fun handleUnload(event: ModuleUnloadedEvent) {
        val module = event.module
        Launcher.instance.commandManager.unregisterCommands(module.cloudModule)
        Launcher.instance.consoleSender.sendProperty("manager.module.unload", module.fileContent.name, module.fileContent.author)
    }

}