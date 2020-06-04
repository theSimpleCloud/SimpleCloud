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

package eu.thesimplecloud.module.proxy.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.proxy.config.Config
import eu.thesimplecloud.module.proxy.config.DefaultConfig
import eu.thesimplecloud.module.proxy.config.ProxyGroupConfiguration
import eu.thesimplecloud.module.proxy.manager.commands.ProxyCommand
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 14.03.2020
 * Time: 21:31
 */
class ProxyModule : ICloudModule{

    val configFile = File("modules/proxy", "config.json")
    lateinit var config: Config

    override fun onEnable() {
        loadConfig()
        Launcher.instance.commandManager
                .registerCommand(this, ProxyCommand(this))
    }

    override fun onDisable() {
    }

    fun loadConfig() {
        if (!configFile.exists()) {
            val config = DefaultConfig.get()
            saveConfig(config)
            this.config = config
        }

        val config = JsonData.fromJsonFile(configFile)!!.getObject(Config::class.java)
        CloudAPI.instance.getSingleSynchronizedObjectManager().updateObject(config)
        this.config = config
    }

    fun saveConfig(config: Config) {
        JsonData.fromObject(config).saveAsFile(configFile)
    }

    fun saveConfig() {
        saveConfig(config)
    }

    fun getProxyConfiguration(serviceGroupName: String): ProxyGroupConfiguration? {
        return config.proxyGroupConfigurations.firstOrNull { it.proxyGroup == serviceGroupName }
    }

}