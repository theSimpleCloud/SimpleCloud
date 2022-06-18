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

package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.launcher.config.launcher.LauncherConfig
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.console.setup.provider.BooleanSetupAnswerProvider
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.IpValidator
import eu.thesimplecloud.launcher.utils.WebsiteContentLoader

class AutoIpSetup : ISetup {

    @SetupQuestion(
        0,
        "Do you want to retrieve your ip automatically via `ipify.org`?",
        BooleanSetupAnswerProvider::class
    )
    fun setup(boolean: Boolean): Boolean {
        if (!boolean) {
            Launcher.instance.setupManager.queueSetup(IpSetup(), true)
            return true
        }

        val ip = try {
            val data = WebsiteContentLoader().loadContent("https://api.ipify.org?format=json")
            JsonLib.fromJsonString(data).getString("ip")
        } catch (e: Exception) {
            Launcher.instance.logger.warning("Unable to connect to \"ipify.org\".")
            return false
        }
        if (ip == null || !IpValidator().validate(ip)) {
            Launcher.instance.logger.warning("Received response can not be parsed to an ip.")
            return false
        }

        val launcherConfig = Launcher.instance.launcherConfig
        val config = LauncherConfig(
            ip,
            launcherConfig.port,
            launcherConfig.language,
            launcherConfig.directoryPaths
        )
        Launcher.instance.replaceLauncherConfig(config)
        return true
    }

}