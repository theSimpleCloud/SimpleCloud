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

package eu.thesimplecloud.module.sign.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.sync.`object`.GlobalPropertyUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.sign.lib.SignModuleConfig
import eu.thesimplecloud.module.sign.manager.command.SignCommand

/**
 * Created by IntelliJ IDEA.
 * Date: 10.10.2020
 * Time: 12:42
 * @author Frederick Baier
 */
class SignModule : ICloudModule {

    override fun onEnable() {
        instance = this

        Launcher.instance.commandManager.registerCommand(this, SignCommand())
        val signModuleConfig = SignModuleConfigPersistence.load()
        SignModuleConfigPersistence.save(signModuleConfig)
        CloudAPI.instance.getGlobalPropertyHolder().setProperty("sign-config", signModuleConfig)

        CloudAPI.instance.getEventManager().registerListener(this, object: IListener {

            @CloudEventHandler
            fun handleUpdate(event: GlobalPropertyUpdatedEvent) {
                if (event.propertyName == "sign-config") {
                    val property = event.property as IProperty<SignModuleConfig>
                    SignModuleConfigPersistence.save(property.getValue())
                }
            }

        })
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var instance: SignModule
            private set
    }

}