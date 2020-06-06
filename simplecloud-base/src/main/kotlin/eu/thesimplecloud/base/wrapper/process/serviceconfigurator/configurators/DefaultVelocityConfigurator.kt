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

package eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.FileEditor
import eu.thesimplecloud.base.core.utils.FileCopier
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.IServiceConfigurator
import java.io.File

class DefaultVelocityConfigurator : IServiceConfigurator {

    override fun configureService(cloudService: ICloudService, serviceTmpDirectory: File) {
        val configFile = File(serviceTmpDirectory, "velocity.toml")
        if (!configFile.exists()) {
            FileCopier.copyFileOutOfJar(configFile, "/files/velocity.toml")
        }
        val fileEditor = FileEditor(configFile)
        fileEditor.replaceLine("bind = \"0.0.0.0:25577\"", "bind = \"0.0.0.0:${cloudService.getPort()}\"")
        fileEditor.replaceLine("show-max-players = 500", "show-max-players = ${cloudService.getMaxPlayers()}")
        fileEditor.save(configFile)
    }


}