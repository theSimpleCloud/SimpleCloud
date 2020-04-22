package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import kotlin.properties.Delegates

class ProxyGroupSetup : DefaultGroupSetup(), ISetup {

    private lateinit var serviceVersion: ServiceVersion
    private var startPort by Delegates.notNull<Int>()
    private lateinit var wrapper: IWrapperInfo
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
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.name.too-long", "The specified name is too long.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.name.success", "Name set.")
        return true
    }

    @SetupQuestion(1, "manager.setup.service-group.question.template", "Which template shall the group use? (create = Creates a template with the group's name)")
    fun templateQuestion(templateName: String): Boolean {
        val template = createTemplate(templateName, this.name)
        if (template != null) {
            this.templateName = template
        }
        return template != null
    }

    @SetupQuestion(2, "manager.setup.proxy-group.question.type", "Which proxy shall the group use? (Bungeecord, Travertine, Waterfall, Hexacord)")
    fun typeQuestion(string: String): Boolean {
        val serviceVersion = JsonData.fromObject(string.toUpperCase()).getObjectOrNull(ServiceVersion::class.java)
        if (serviceVersion == null || serviceVersion.serviceVersionType != ServiceVersion.ServiceVersionType.PROXY_DEFAULT) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.version.unsupported", "The specified version is not supported.")
            return false
        }
        this.serviceVersion = serviceVersion
        Launcher.instance.consoleSender.sendMessage("manager.setup.proxy-group.question.type.success", "Proxy version set.")
        return true
    }

    @SetupQuestion(4, "manager.setup.service-group.question.memory", "How much memory shall the server group have?")
    fun memoryQuestion(memory: Int): Boolean {
        if (memory < 128) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.memory.too-low", "The specified amount of memory is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.memory.success", "Memory set.")
        this.memory = memory
        return true
    }

    @SetupQuestion(5, "manager.setup.service-group.question.max-players", "How many players shall be able to join the server at most?")
    fun maxPlayersQuestion(maxPlayers: Int): Boolean {
        if (maxPlayers < 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.max-players.too-low", "The specified amount of players is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.max-players.success", "Max-Players set.")
        this.maxPlayers = maxPlayers
        return true
    }


    @SetupQuestion(6, "manager.setup.service-group.question.minimum-online", "How many services shall always be online? (in LOBBY state)")
    fun minimumOnlineQuestion(minimumOnlineServices: Int): Boolean {
        if (minimumOnlineServices < 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.minimum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.minimum-online.success", "Min-Online-Services set.")
        this.minimumOnlineServices = minimumOnlineServices
        return true
    }

    @SetupQuestion(7, "manager.setup.service-group.question.maximum-online", "How many services shall be online at most? (unlimited = -1)")
    fun maximumOnlineQuestion(maximumOnlineServices: Int): Boolean {
        if (maximumOnlineServices < -1) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.maximum-online.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.maximum-online.success", "Max-Online-Services set.")
        this.maximumOnlineServices = maximumOnlineServices
        return true
    }

    @SetupQuestion(8, "manager.setup.service-group.question.static", "Shall this server group be static? (yes / no)")
    fun staticQuestion(static: Boolean) {
        this.static = static
    }

    @SetupQuestion(9, "manager.setup.proxy-group.question.wrapper", "On which wrapper shall services of this group run?")
    fun wrapperQuestion(string: String): Boolean {
        val wrapper = CloudAPI.instance.getWrapperManager().getWrapperByName(string)?.obj
        if (wrapper == null){
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.wrapper.not-exist", "The specified wrapper does not exist.")
            return false
        }
        this.wrapper = wrapper
        return true
    }

    @SetupQuestion(10, "manager.setup.service-group.question.percent", "How full shall a service of this server group be until a new service starts? (in percent)")
    fun percentQuestion(percent: Int): Boolean {
        if (percent < 1 || percent > 100) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.percent.out-of-range", "The specified number is out of range.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.percent.success", "Percent to start a new service set.")
        this.percent = percent
        return true
    }

    @SetupQuestion(11, "manager.setup.proxy-group.question.start-port", "On which port should proxies of this group start?")
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
        CloudAPI.instance.getCloudServiceGroupManager().createProxyGroup(name, templateName, memory, maxPlayers, minimumOnlineServices, maximumOnlineServices, true, static, percent, wrapper.getName(), startPort, serviceVersion, 10)
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.finished", "Group %NAME%", name, " created.")
    }


}