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
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import kotlin.properties.Delegates

class LobbyGroupSetup : DefaultGroupSetup(), ISetup {

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

    @SetupQuestion(0, "manager.setup.service-group.question.name", "Which name shall the group have?")
    fun nameQuestion(name: String): Boolean {
        this.name = name
        if (name.length > 16) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.name.too-long", "The specified name is too long.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.name.success", "Name set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.service-group.question.template", "Which template shall the group use? (create = Creates a template with the group's name)", GroupTemplateSetupAnswerProvider::class)
    fun templateQuestion(templateName: String): Boolean {
        val template = createTemplate(templateName, this.name)
        if (template != null) {
            this.templateName = template
        }
        return template != null
    }

    @SetupQuestion(2, "manager.setup.service-group.question.type", "Which server version shall the group use?", ServerVersionTypeSetupAnswerProvider::class)
    fun typeQuestion(string: String) {
        this.serviceVersion = CloudAPI.instance.getServiceVersionHandler().getServiceVersionByName(string)!!
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.server-group.question.type.success", "Spigot set.")
    }

    @SetupQuestion(4, "manager.setup.service-group.question.memory", "How much memory shall the server group have? (MB)")
    fun memoryQuestion(memory: Int): Boolean {
        if (memory < 128) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.memory.too-low", "The specified amount of memory is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.memory.success", "Memory set.")
        this.memory = memory
        return true
    }

    @SetupQuestion(5, "manager.setup.service-group.question.max-players", "How many players shall be able to join the server at most?")
    fun maxPlayersQuestion(maxPlayers: Int): Boolean {
        if (maxPlayers < 0) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.max-players.too-low", "The specified amount of players is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.max-players.success", "Max-Players set.")
        this.maxPlayers = maxPlayers
        return true
    }


    @SetupQuestion(6, "manager.setup.service-group.question.minimum-online", "How many services shall always be online? (VISIBLE)")
    fun minimumOnlineQuestion(minimumOnlineServices: Int): Boolean {
        if (minimumOnlineServices < 0) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.minimum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.minimum-online.success", "Min-Online-Services set.")
        this.minimumOnlineServices = minimumOnlineServices
        return true
    }

    @SetupQuestion(7, "manager.setup.service-group.question.maximum-online", "How many services shall be online at most? (unlimited = -1)")
    fun maximumOnlineQuestion(maximumOnlineServices: Int): Boolean {
        if (maximumOnlineServices < -1) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.maximum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.maximum-online.success", "Max-Online-Services set.")
        this.maximumOnlineServices = maximumOnlineServices
        return true
    }

    @SetupQuestion(8, "manager.setup.service-group.question.static", "Shall this server group be static?", BooleanSetupAnswerProvider::class)
    fun staticQuestion(static: Boolean) {
        this.static = static
    }

    @SetupQuestion(9, "manager.setup.server-group.question.wrapper", "On which wrapper shall services of this group run? (Needed when the group is static. Otherwise you can leave it empty for the wrapper with the lowest workload.)", GroupWrapperSetupAnswerProvider::class)
    fun wrapperQuestion(string: String): Boolean {
        if (string.isBlank() && static)
            return false
        if (string.isBlank() && !static)
            return true
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(string)!!
        this.wrapper = wrapper
        return true
    }

    @SetupQuestion(10, "manager.setup.service-group.question.percent", "How full shall a service of this server group be until a new service starts? (in percent)")
    fun percentQuestion(percent: Int): Boolean {
        if (percent < 1 || percent > 100) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.percent.out-of-range", "The specified number is out of range.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.percent.success", "Percent to start a new service set.")
        this.percent = percent
        return true
    }

    @SetupQuestion(11, "manager.setup.service-group.question.priority", "Which priority shall this lobby group have? (0 = default)")
    fun priorityQuestion(priority: Int): Boolean {
        if (priority < 0) {
            Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.question.priority.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.priority.success", "Priority set.")
        this.priority = priority
        return true
    }

    @SetupQuestion(12, "manager.setup.service-group.question.permission", "Which permission shall a player need to join this group? (leave it empty for no permission)")
    fun permissionQuestion(permission: String) {
       handlePermission(permission)
    }

    @SetupFinished
    fun finished() {
        CloudAPI.instance.getCloudServiceGroupManager().createLobbyGroup(name, templateName, memory, maxPlayers, minimumOnlineServices, maximumOnlineServices, true, static, percent, wrapper?.getName(), priority, permission, serviceVersion, 9)
        Launcher.instance.consoleSender.sendMessage(true, "manager.setup.service-group.finished", "Group %NAME%", name, " created.")
    }


}