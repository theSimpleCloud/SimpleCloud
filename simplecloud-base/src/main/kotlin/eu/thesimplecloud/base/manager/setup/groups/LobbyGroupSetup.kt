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

package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.base.manager.setup.groups.provider.GroupTemplateSetupAnswerProvider
import eu.thesimplecloud.base.manager.setup.groups.provider.GroupWrapperSetupAnswerProvider
import eu.thesimplecloud.base.manager.setup.groups.provider.ServerVersionTypeSetupAnswerProvider
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.console.setup.provider.BooleanSetupAnswerProvider
import eu.thesimplecloud.launcher.startup.Launcher
import kotlin.properties.Delegates

open class LobbyGroupSetup : DefaultGroupSetup(), ISetup {

    private lateinit var serviceVersion: ServiceVersion
    private var wrapper: IWrapperInfo? = null
    private var priority by Delegates.notNull<Int>()
    private var percent by Delegates.notNull<Int>()
    private var static by Delegates.notNull<Boolean>()
    private var maximumOnlineServices by Delegates.notNull<Int>()
    private var minimumOnlineServices by Delegates.notNull<Int>()
    private var maxPlayers by Delegates.notNull<Int>()
    private var memory by Delegates.notNull<Int>()
    private lateinit var name: String
    private lateinit var templateName: String
    var javaCommand: String = "java"

    @SetupQuestion(0, "manager.setup.service-group.question.name")
    fun nameQuestion(name: String): Boolean {
        this.name = name
        if (name.length > 32) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.name.too-long")
            return false
        }
        if (name.isEmpty()) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.name.is-empty")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.name.success")
        return true
    }

    @SetupQuestion(1, "manager.setup.service-group.question.template", GroupTemplateSetupAnswerProvider::class)
    fun templateQuestion(templateName: String): Boolean {
        val template = createTemplate(templateName, this.name)
        if (template != null) {
            this.templateName = template
        }
        return template != null
    }

    @SetupQuestion(2, "manager.setup.service-group.question.type", ServerVersionTypeSetupAnswerProvider::class)
    fun typeQuestion(string: String) {
        this.serviceVersion = CloudAPI.instance.getServiceVersionHandler().getServiceVersionByName(string)!!
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.server-group.question.type.success")
    }

    @SetupQuestion(4, "manager.setup.service-group.question.memory")
    fun memoryQuestion(memory: Int): Boolean {
        if (memory < 128) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.memory.too-low")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.memory.success")
        this.memory = memory
        return true
    }

    @SetupQuestion(5, "manager.setup.service-group.question.max-players")
    fun maxPlayersQuestion(maxPlayers: Int): Boolean {
        if (maxPlayers < 0) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.max-players.too-low")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.max-players.success")
        this.maxPlayers = maxPlayers
        return true
    }


    @SetupQuestion(6, "manager.setup.service-group.question.minimum-online")
    fun minimumOnlineQuestion(minimumOnlineServices: Int): Boolean {
        if (minimumOnlineServices < 0) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.minimum-online.too-low")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.minimum-online.success")
        this.minimumOnlineServices = minimumOnlineServices
        return true
    }

    @SetupQuestion(7, "manager.setup.service-group.question.maximum-online")
    fun maximumOnlineQuestion(maximumOnlineServices: Int): Boolean {
        if (maximumOnlineServices < -1) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.maximum-online.too-low")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.maximum-online.success")
        this.maximumOnlineServices = maximumOnlineServices
        return true
    }

    @SetupQuestion(8, "manager.setup.service-group.question.static", BooleanSetupAnswerProvider::class)
    fun staticQuestion(static: Boolean) {
        this.static = static
    }

    @SetupQuestion(9, "manager.setup.server-group.question.wrapper", GroupWrapperSetupAnswerProvider::class)
    fun wrapperQuestion(string: String): Boolean {
        if (string.isBlank() && static)
            return false
        if (string.isBlank() && !static)
            return true
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(string)!!
        this.wrapper = wrapper
        return true
    }

    @SetupQuestion(10, "manager.setup.service-group.question.percent")
    fun percentQuestion(percent: Int): Boolean {
        if (percent < 1 || percent > 100) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.percent.out-of-range")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.percent.success")
        this.percent = percent
        return true
    }

    @SetupQuestion(11, "manager.setup.service-group.question.priority")
    fun priorityQuestion(priority: Int): Boolean {
        if (priority < 0) {
            Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.question.priority.too-low")
            return false
        }
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.priority.success")
        this.priority = priority
        return true
    }

    @SetupQuestion(12, "manager.setup.service-group.question.permission")
    fun permissionQuestion(permission: String) {
        handlePermission(permission)
    }

    @SetupFinished
    fun finished() {
        CloudAPI.instance.getCloudServiceGroupManager().createLobbyGroup(
            name,
            templateName,
            memory,
            maxPlayers,
            minimumOnlineServices,
            maximumOnlineServices,
            false,
            static,
            false,
            percent,
            wrapper?.getName(),
            priority,
            permission,
            serviceVersion,
            9,
            javaCommand
        )
        Launcher.instance.consoleSender.sendPropertyInSetup("manager.setup.service-group.finished", name)
    }
}