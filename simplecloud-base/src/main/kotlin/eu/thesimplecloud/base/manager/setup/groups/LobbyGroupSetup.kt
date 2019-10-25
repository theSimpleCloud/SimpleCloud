package eu.thesimplecloud.base.manager.setup.groups

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupFinished
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.Downloader
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.service.ServiceVersion
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import java.util.logging.Logger
import kotlin.properties.Delegates

class LobbyGroupSetup : ISetup {

    private lateinit var serviceVersion: ServiceVersion
    private var wrapper: IWrapperInfo? = null
    private var permission: String? = null
    private var priority by Delegates.notNull<Int>()
    private var percent by Delegates.notNull<Int>()
    private var static by Delegates.notNull<Boolean>()
    private var maximumOnlineServices by Delegates.notNull<Int>()
    private var minimumOnlineServices by Delegates.notNull<Int>()
    private var maxPlayers by Delegates.notNull<Int>()
    private var memory by Delegates.notNull<Int>()
    private lateinit var name: String
    private lateinit var templateName: String
    private lateinit var type: String

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

    @SetupQuestion(1, "manager.setup.service-group.question.template", "Which template shall the group use?")
    fun templateQuestion(template: ITemplate) {
        this.templateName = template.getName()
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.template.success", "Template set.")
    }

    @SetupQuestion(2, "manager.setup.service-group.question.type", "Which spigot shall the group use? (Spigot, Paper)")
    fun typeQuestion(string: String) {
        if (!string.equals("paper", true) && !type.equals("spigot", true)){
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.type.invalid", "Invalid response.")
            return
        }
        this.type = string
        Launcher.instance.consoleSender.sendMessage("manager.setup.server-group.question.type.success", "Spigot set.")
    }

    @SetupQuestion(3, "manager.setup.service-group.question.version", "Which version to you want to use? (1.7.10, 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4)")
    fun versionQuestion(answer: String) : Boolean {
        val version = answer.replace(".", "_")
        val serviceVersion = JsonData.fromObject(type.toUpperCase() + "_" + version).getObjectOrNull(ServiceVersion::class.java)
        if (serviceVersion == null) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.version.unsupported", "The specified version is not supported.")
            return false
        }
        this.serviceVersion = serviceVersion
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.version.success", "Version set.")
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

    @SetupQuestion(9, "manager.setup.server-group.question.wrapper", "On which wrapper shall services of this group run? (Needed when the group is static. Otherwise you can leave it empty for the wrapper with the lowest workload.)")
    fun wrapperQuestion(string: String): Boolean {
        if (string.isBlank())
            return true
        val wrapper = CloudLib.instance.getWrapperManager().getWrapperByName(string)
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

    @SetupQuestion(11, "manager.setup.service-group.question.priority", "Which priority shall this lobby group have? (0 = default)")
    fun priorityQuestion(priority: Int): Boolean {
        if (priority < 0) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.question.priority.too-low", "The specified number is too low.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.priority.success", "Priority set.")
        this.priority = priority
        return true
    }

    @SetupQuestion(12, "manager.setup.service-group.question.permission", "Which permission shall a player need to join this group? (leave it empty for no permission)")
    fun permissionQuestion(permission: String) {
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.permission.success", "Permission set.")
        if (permission.isNotBlank())
            this.permission = permission
    }

    @SetupFinished
    fun finished() {
        CloudLib.instance.getCloudServiceGroupManager().createLobbyGroup(name, templateName, memory, maxPlayers, minimumOnlineServices, maximumOnlineServices, true, static, percent, wrapper?.getName(), priority, permission, serviceVersion, 0)
        Launcher.instance.consoleSender.sendMessage("manager.setup.service-group.finished", "Group %NAME%", name, " created.")
    }


}