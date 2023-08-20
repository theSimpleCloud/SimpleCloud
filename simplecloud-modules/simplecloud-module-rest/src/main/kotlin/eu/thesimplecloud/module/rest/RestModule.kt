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

package eu.thesimplecloud.module.rest

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.rest.auth.JwtProvider
import eu.thesimplecloud.module.rest.config.RestConfig
import eu.thesimplecloud.module.rest.config.RestConfigLoader
import eu.thesimplecloud.module.rest.javalin.RestServer
import eu.thesimplecloud.module.rest.util.executeWithDifferentContextClassLoader

/**
 * Created by IntelliJ IDEA.
 * Date: 04.10.2020
 * Time: 14:57
 * @author Frederick Baier
 */
class RestModule : ICloudModule {

    lateinit var server: RestServer

    override fun onEnable() {
        val config = loadConfig()
        val moduleClassLoader = this::class.java.classLoader
        executeWithDifferentContextClassLoader(moduleClassLoader) {
            startRestServer(config)
        }
        Launcher.instance.consoleSender.sendProperty("module.rest.loaded", config.port.toString())
    }

    private fun loadConfig(): RestConfig {
        return RestConfigLoader().loadConfig()
    }

    private fun startRestServer(config: RestConfig) {
        JwtProvider(config.secret)
        this.server = RestServer(config.port)
    }

    override fun onDisable() {
        server.shutdown()
    }
}