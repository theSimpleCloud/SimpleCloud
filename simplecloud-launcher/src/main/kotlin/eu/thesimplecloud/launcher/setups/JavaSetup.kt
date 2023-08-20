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

package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.config.java.JavaVersion
import eu.thesimplecloud.launcher.config.java.JavaVersionConfigLoader
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher

class JavaSetup : ISetup {

    lateinit var name: String
    lateinit var path: String

    @SetupQuestion(0, "manager.setup.service-versions.question.javaName")
    fun javaNameSetup(name: String): Boolean {
        return if (name.isNotEmpty()) {
            this.name = name
            true
        } else {
            false
        }
    }

    @SetupQuestion(1, "manager.setup.service-versions.question.java")
    fun javaVersionSetup(path: String): Boolean {
        return if (path.isNotEmpty()) {
            this.path = path
            setup()
            true
        } else {
            false
        }
    }

    fun setup() {
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-versions.question.java.success")
        val javaVersion = JavaVersion.paths
        javaVersion.versions[name] = path
        JavaVersionConfigLoader().saveConfig(javaVersion)
    }

}