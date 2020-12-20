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

package eu.thesimplecloud.launcher.external.module.handler

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.exception.module.ModuleLoadException
import eu.thesimplecloud.launcher.external.module.LoadedModule
import eu.thesimplecloud.launcher.external.module.ModuleClassLoader

/**
 * Created by IntelliJ IDEA.
 * Date: 27.11.2020
 * Time: 21:04
 * @author Frederick Baier
 */
class UnsafeModuleUnloader(val loadedModule: LoadedModule) {

    fun unload() {
        val cloudModule = loadedModule.cloudModule
        try {
            cloudModule.onDisable()
        } catch (ex: Exception) {
            throw ModuleLoadException("Error while unloading module ${loadedModule.getName()}", ex)
        }

        //unregister all listeners etc.
        CloudAPI.instance.getEventManager().unregisterAllListenersByCloudModule(cloudModule)
        (loadedModule.moduleClassLoader as ModuleClassLoader).close()

        //unregister all message channel
        CloudAPI.instance.getMessageChannelManager().unregisterMessageChannel(cloudModule)

        CloudAPI.instance.getEventManager().call(ModuleUnloadedEvent(loadedModule))

        //reset all property values
        CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().forEach { player ->
            player.getProperties().forEach { (it.value as Property).resetValue() }
        }
        CloudAPI.instance.getCloudServiceManager().getAllCachedObjects().forEach { group ->
            group.getProperties().forEach { (it.value as Property).resetValue() }
        }

        CloudAPI.instance.getLanguageManager().unregisterLanguageFileByCloudModule(cloudModule)
    }

}