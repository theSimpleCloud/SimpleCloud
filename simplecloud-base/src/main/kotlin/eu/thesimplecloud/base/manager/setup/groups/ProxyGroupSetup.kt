package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import java.io.File
import kotlin.properties.Delegates

class ProxyGroupSetup : ISetup {

    private lateinit var wrapperName: String
    private var startPort by Delegates.notNull<Int>()
    private var percent by Delegates.notNull<Int>()
    private var static by Delegates.notNull<Boolean>()
    private var maximumOnlineServices by Delegates.notNull<Int>()
    private var minimumOnlineServices by Delegates.notNull<Int>()
    private var maxPlayers by Delegates.notNull<Int>()
    private var memory by Delegates.notNull<Int>()
    private lateinit var name: String
    private lateinit var templateName: String

    @SetupQuestion(0, "manager.setup.service-group.question.name", "Which name should the group have")
    fun nameQuestion(name: String): Boolean {
        this.name = name
        if (name.length > 16) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.name.too-long", "The specified name is too long.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.name.success", "Name set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.service-group.question.template", "Which template should the group have")
    fun templateQuestion(template: ITemplate) {
        this.templateName = template.getName()
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.template.success", "Template set.")
    }

    @SetupQuestion(2, "manager.setup.service-group.question.memory", "How much memory should the server group have")
    fun memoryQuestion(memory: Int): Boolean {
        if (memory < 128) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.memory.too-low", "The specified amount of memory is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.memory.success", "Memory set.")
        this.memory = memory
        return true
    }

    @SetupQuestion(3, "manager.setup.service-group.question.max-players", "How much players should be able to join the server at most.")
    fun maxPlayersQuestion(maxPlayers: Int): Boolean {
        if (maxPlayers < 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.max-players.too-low", "The specified amount of players is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.max-players.success", "Max-Players set.")
        this.maxPlayers = maxPlayers
        return true
    }


    @SetupQuestion(4, "manager.setup.service-group.question.minimum-online", "How much services should always be online (in LOBBY state)")
    fun minimumOnlineQuestion(minimumOnlineServices: Int): Boolean {
        if (minimumOnlineServices < 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.minimum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.minimum-online.success", "Min-Online-Services set.")
        this.minimumOnlineServices = minimumOnlineServices
        return true
    }

    @SetupQuestion(5, "manager.setup.service-group.question.maximum-online", "How much services should be online at most  (unlimited = -1)")
    fun maximumOnlineQuestion(maximumOnlineServices: Int): Boolean {
        if (maximumOnlineServices < -1) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.maximum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.maximum-online.success", "Max-Online-Services set.")
        this.maximumOnlineServices = maximumOnlineServices
        return true
    }

    @SetupQuestion(6, "manager.setup.service-group.question.static", "Should this server group be static (yes / no)")
    fun staticQuestion(static: Boolean) {
        this.static = static
    }

    @SetupQuestion(7, "manager.setup.service-group.question.percent", "How full should a service of this server group be until a new service starts (in percent)")
    fun percentQuestion(percent: Int): Boolean {
        if (percent < 1 || percent > 100) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.percent.out-of-range", "The specified number is out of range.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.percent.success", "Percent to start a new service set.")
        this.percent = percent
        return true
    }

    @SetupQuestion(8, "manager.setup.proxy-group.question.wrapper", "On which wrapper should services of this poxy group start")
    fun wrapperQuestion(wrapper: IWrapperInfo): Boolean {
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.wrapper.success", "Wrapper set.")
        this.wrapperName = wrapper.getName()
        return true
    }

    @SetupQuestion(9, "manager.setup.proxy-group.question.start-port", "Please provide the start port of this proxy group")
    fun startPortQuestion(startPort: Int): Boolean {
        if (startPort < 100 || startPort > 65535) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.port.out-of-range", "The specified port is out of range.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.port.success", "Start-Port set.")
        this.startPort = startPort
        return true
    }

    @SetupFinished
    fun finished() {
        CloudLib.instance.getCloudServiceGroupManager().createProxyGroup(name, templateName, memory, maxPlayers, minimumOnlineServices, maximumOnlineServices, true, static, percent, wrapperName, startPort)
        File(CloudLib.instance.getTemplateManager().getTemplate(this.templateName)!!.getDirectory(), name).mkdirs()
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.finished", "Group created.")
    }


}