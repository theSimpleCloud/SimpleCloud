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

package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.IpValidator

class WrapperSetup : ISetup {

    private var maxSimultaneouslyStartingServices: Int = 0
    private var memory: Int = 0
    private lateinit var host: String
    private lateinit var name: String

    @SetupQuestion(0, "manager.setup.wrapper.question.name", "Which name should the wrapper have?")
    fun nameSetup(name: String): Boolean {
        if (name.length > 16) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.name.too-long", "The specified name ist ioo long.")
            return false
        }
        this.name = name
        Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.name.success", "Name set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.wrapper.question.host", "On which host starts the wrapper? (this = ip of this server)")
    fun hostSetup(host: String): Boolean {
        if (host.equals("this", true)){
            this.host = Launcher.instance.launcherConfigLoader.loadConfig().host
            Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.host.success", "Host set.")
            return true
        }
        if (!IpValidator().validate(host)) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.host.invalid", "The specified host is invalid.")
            return false
        }
        this.host = host
        Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.host.success", "Host set.")
        return true
    }

    @SetupQuestion(2, "manager.setup.wrapper.question.memory", "How much memory does the wrapper have?")
    fun memorySetup(memory: Int): Boolean {
        if (memory < 1024) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.memory.too-low", "The specified amount of memory is too low.")
            return false
        }
        this.memory = memory
        Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.memory.success", "Memory set.")
        return true
    }

    @SetupQuestion(3, "manager.setup.wrapper.question.start-services", "How much services should the wrapper can start simultaneously?")
    fun simultaneouslySetup(maxSimultaneouslyStartingServices: Int): Boolean {
        if (maxSimultaneouslyStartingServices < 1) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.start-services.too-low", "The specified amount is too low.")
            return false
        }
        this.maxSimultaneouslyStartingServices = maxSimultaneouslyStartingServices
        Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.question.start-services.success", "Max simultaneously starting services set.")
        return true
    }

    @SetupFinished
    fun finished(){
        val wrapperInfo = DefaultWrapperInfo(name, host, maxSimultaneouslyStartingServices, memory)
        CloudAPI.instance.getWrapperManager().update(wrapperInfo)
        Launcher.instance.consoleSender.sendMessage("manager.setup.wrapper.finished", "Wrapper %NAME%", name, " created.")
    }



}