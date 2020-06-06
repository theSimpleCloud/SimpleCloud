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

package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.utils.Downloader
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class ServerJarSetup(private val serverJar: File) : ISetup {

    private var spigotType: String = ""

    @SetupQuestion(0, "manager.setup.server-jar.type.question", "Which server version do you want to use? (Spigot, Paper)")
    fun setup(answer: String): Boolean {
        this.spigotType = answer.toUpperCase()
        if (!(spigotType == "SPIGOT" || spigotType == "PAPER")) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.type.invalid", "The specified type is invalid.")
            return false
        }
        return true
    }

    @SetupQuestion(1, "manager.setup.server-jar.version.question", "Which version to you want to use? (1.7.10, 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4, 1.15.2)")
    fun versionSetup(answer: String) : Boolean {
        val version = answer.replace(".", "_")
        val serviceVersion = JsonData.fromObject(spigotType + "_" + version).getObjectOrNull(ServiceVersion::class.java)
        if (serviceVersion == null) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.version.unsupported", "The specified version is not supported.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.downloading", "Downloading server...")
        Downloader().userAgentDownload(serviceVersion.downloadLink, serverJar)
        return true
    }


}
